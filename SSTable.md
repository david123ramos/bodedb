|================SSTABLE==============|
|---------HEADER----------------------|
| MAGIC_NUMBER: 0xB0DEDB (Bode db)    |
|-------------------------------------|
|------------DATA BLOCKS -------------|
| |BLOCK 1---------------------|      |
| | key1 - value1              |      |
| | key2 - value2              |      |
| | key3 - value3              |      |
| |----------------------------|      |
| | 0xB0DECF checksum          |      |
| |----------------------------|      |
| |BLOCK N---------------------|      |
| | keyN - valueN              |      |
| | keyN2 - valueN2            |      |
| | keyN3 - valueN3            |      |
| |----------------------------|      |
| | 0xB0DECF checksum          |      |
| |----------------------------|      |
|-------------------------------------|
|INDEX -------------------------------|
| key1 @ offset 4931 @ block 1        |
| keyN @ offset 8741 @ block N        |
| index checksum                      |
|-------------------------------------|
|Bloom FILTER ------------------------|
| entries: [1,2,4,5...N]              |
| bucket: 3                           |
| finger: 3bits                       |
|-------------------------------------|
|Footer-------------------------------|
|index @ offset 1023912               |
|index_size: 1231231344               |
|Bloom_filter @ offset 1209483        |
|Bloom_filter_size: 19234             |
|MAGIC_NUMBER: 0xB0DEBDED (bode db END)
|=====================================|




