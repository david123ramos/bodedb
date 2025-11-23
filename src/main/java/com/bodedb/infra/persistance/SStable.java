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
import java.util.TreeMap;
import java.util.zip.CRC32;

public class SStable<K,V> {
    private final SortedMap<K,V> map;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;
    public final static String ROOT_DATA_PATH = "data/";

    public SStable(String path, Serializer<K> keySerializer, Serializer<V> valueSerializer) throws Exception {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.map = readFromDisk(path);
    }

    public SStable(SortedMap<K, V> map, Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        this.map = map;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    /*
    * |================SSTABLE==============|
    * |---------HEADER----------------------|
    * | MAGIC_NUMBER: 0xB0DEDB (Bode db)|
    * |-------------------------------------|
    * |DATA BLOCKS -------------------------|
    * |      |BLOCK 1---------------------| |
    * |      |    key1 - value1           | |
    * |      |    key2 - value2           | |
    * |      |    key3 - value3           | |
    * |      |----------------------------| |
    * |      | block1 checksum            | |
    * |      |----------------------------| |
    * |      |BLOCK N---------------------| |
    * |      |    keyN - valueN           | |
    * |      |    keyN2 - valueN2         | |
    * |      |    keyN3 - valueN3         | |
    * |      |----------------------------| |
    * |      | block1 checksum            | |
    * |      |----------------------------| |
    * |-------------------------------------|
    * |INDEX -------------------------------|
    * | key1 @ offset 4931 @ block 1        |
    * | keyN @ offset 8741 @ block N        |
    * | index checksum                      |
    * |-------------------------------------|
    * |Bloom FILTER ------------------------|
    * | entries: [1,2,4,5...N]              |
    * | bucket: 3                           |
    * | finger: 3bits                       |
    * |-------------------------------------|
    * |Footer-------------------------------|
    * |index @ offset 1023912               |
    * |index_size: 1231231344               |
    * |Bloom_filter @ offset 1209483        |
    * |Bloom_filter_size: 19234             |
    * |MAGIC_NUMBER: 0xB0DEBDED (bode db END)
    * |=====================================|
    * */
    public void writeToFile() {
        System.out.println("[SSTable] Saving memtable in sstable file");
        String tablename = "table/sstable_"+ LocalDateTime.now().toInstant(ZoneOffset.UTC)+".sst";

        File file = new File(ROOT_DATA_PATH + tablename);

        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {

            writeHeader(dos);
            StringBuilder sb = new StringBuilder();
            int counter = 0;

            for (Map.Entry<K, V> entry : this.map.entrySet()) {
                sb.append(entry.getKey());

                if(counter >= 100) {
                    writeBlockCheckSum(sb, dos);
                    sb = new StringBuilder();
                    counter = 0;
                }

                keySerializer.write(entry.getKey(), dos);
                valueSerializer.write(entry.getValue(), dos);

                counter++;
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

    void writeBlockCheckSum(StringBuilder sb, DataOutputStream  dos) throws IOException {
        CRC32 crc32 = new CRC32();
        crc32.update(sb.toString().getBytes(StandardCharsets.UTF_8));
        dos.writeInt(0xB0DECF);
        dos.writeLong(crc32.getValue());
    }

    //footer specifies and magicNumber of EOF, index pointing to index position at file
    //bloom, pointing to start of definition of bloom filter at file and their respective sizes
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

    private SortedMap<K,V> readFromDisk(String sstablePath) throws Exception {

        File file = new File(sstablePath);
        SortedMap<K, V> result = new TreeMap();

        if(!file.exists()) throw new FileNotFoundException("[SSTable] No SSTable with "+sstablePath+" was found");

        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

        while (dis.available() > 0) {
            K key = keySerializer.read(dis);
            V value = valueSerializer.read(dis);

            result.put(key, value);
        }

        return result;
    }

    private void updateCurrentFile(String tablename) {
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(Path.of(ROOT_DATA_PATH+"/CURRENT.txt").toFile())))) {
            byte[] bytes = tablename.getBytes(StandardCharsets.UTF_8);
            dos.writeInt(bytes.length);
            dos.write(bytes);
            dos.flush();

        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public SortedMap<K, V> getMap() {
        return map;
    }
}
