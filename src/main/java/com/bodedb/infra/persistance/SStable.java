package com.bodedb.infra.persistance;

import com.bodedb.infra.persistance.serialization.Serializer;
import com.bodedb.infra.persistance.sstable.Footer;
import com.bodedb.infra.persistance.sstable.Header;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.SortedMap;
import java.util.zip.CRC32;

public class SStable<K, V> {
    private final SortedMap<K, V> map;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;
    public final static String ROOT_DATA_PATH = "data/";

    public SStable(String path, Serializer<K> keySerializer, Serializer<V> valueSerializer) throws Exception {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.map = null;
    }

    public SStable(SortedMap<K, V> map, Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        this.map = map;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    /**
     * @see SSTable.md
     */
    public void writeToFile() {
        System.out.println("[SSTable] Saving memtable in sstable file");
        String tablename = "table/sstable_" + LocalDateTime.now().toInstant(ZoneOffset.UTC) + ".sst";

        File file = new File(ROOT_DATA_PATH + tablename);

        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {

            writeHeader(dos);
            StringBuilder sb = new StringBuilder();
            int counter = 0;

            for (Map.Entry<K, V> entry : this.map.entrySet()) {

                sb.append(entry.getKey());

                keySerializer.write(entry.getKey(), dos);
                valueSerializer.write(entry.getValue(), dos);
                counter++;

                if (counter == 3) {
                    writeBlockCheckSum(sb, dos);
                    sb.setLength(0);
                    counter = 0;
                }

            }

            if (counter > 0) {
                writeBlockCheckSum(sb, dos);
            }

            writeFooter(dos);

            dos.flush();
            System.out.println("[SSTable] Flushed binary SSTable: " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }

        updateCurrentFile(tablename);
    }

    void writeHeader(DataOutputStream out) throws IOException {
        out.writeInt(Header.MAGIC_NUMBER);
    }

    void writeBlockCheckSum(StringBuilder sb, DataOutputStream dos) throws IOException {
        CRC32 crc32 = new CRC32();
        crc32.update(sb.toString().getBytes(StandardCharsets.UTF_8));
        dos.writeInt(0xB0DECF);
        dos.writeLong(crc32.getValue());
    }

    // footer specifies and magicNumber of EOF, index pointing to index position at
    // file
    // bloom, pointing to start of definition of bloom filter at file and their
    // respective sizes
    // each memory location occupies only 8 bytes each.
    void writeFooter(DataOutputStream dos) throws IOException {

        dos.writeInt("INDEX".length());
        dos.write(new byte[8]);

        dos.writeInt("INDEX_SIZE".length());
        dos.write(new byte[8]);

        dos.writeInt("BLOOM".length());
        dos.write(new byte[8]);

        dos.writeInt("BLOOM_SIZE".length());
        dos.write(new byte[8]);

        dos.writeInt(Footer.MAGIC_NUMBER);
    }

    private void updateCurrentFile(String tablename) {
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(Path.of(ROOT_DATA_PATH + "/CURRENT.txt").toFile())))) {
            byte[] bytes = tablename.getBytes(StandardCharsets.UTF_8);
            dos.writeInt(bytes.length);
            dos.write(bytes);
            dos.flush();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public SortedMap<K, V> getMap() {
        return map;
    }
}
