# JavaVM Compression Library Benchmark & Comparison

2018/03/22 2:06:36
Takami Torao @ graphite
Java HotSpot(TM) 64-Bit Server VM Oracle Corporation 25.161-b12

## Zero-filled `Byte[104857600]` Array

datasize=100MB, entropy=0.000

| Name | Version | Method | Rate | Compress [MB/sec] | Decompress [MB/sec] | ERR |
|:-----|:--------|:-------|-----:|------------------:|--------------------:|:----|
| java:*uncompress* | Java 1.8.0_161 | buffer | 0.0% | 6,200.7 | 7,950.5 |  |
| java:*uncompress* | Java 1.8.0_161 | stream | 0.0% | 7,600.0 | 4,177.5 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | buffer | 95.3% | 5,600.5 | 2,399.5 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | stream | 95.3% | 3,704.8 | 1,739.7 |  |
| org.lz4:lz4-java | 1.4.1 | buffer | 99.6% | 7,534.0 | 387.1 |  |
| org.lz4:lz4-java | 1.4.1 | stream | 99.6% | 2,543.1 | 336.7 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | buffer | 99.991% | 9,021.9 | 4,858.1 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | stream | 99.991% | 4,096.3 | 2,887.6 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | buffer | 99.991% | 8,660.5 | 4,863.7 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | stream | 99.991% | 5,518.4 | 2,882.2 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | buffer | 99.992% | 1,703.4 | 5,431.3 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | stream | 99.992% | 2,098.7 | 2,928.0 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | buffer | 99.992% | 897.5 | 5,437.5 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | stream | 99.992% | 1,371.8 | 2,906.8 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | buffer | 99.992% | 2,398.6 | 5,466.3 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | stream | 99.992% | 2,089.2 | 2,897.1 |  |
| java:zlib | Java 1.8.0_161 | stream | 99.90% | 162.0 | 473.2 |  |
| java:gzip | Java 1.8.0_161 | stream | 99.90% | 171.9 | 537.0 |  |
| org.apache.commons:commons-compress (bzip2) | 1.16.1 | stream | 99.9999% | 80.4 | 178.7 |  |

## Uniform Random filled `Byte[104857600]` Array

datasize=100MB, entropy=5.545

| Name | Version | Method | Rate | Compress [MB/sec] | Decompress [MB/sec] | ERR |
|:-----|:--------|:-------|-----:|------------------:|--------------------:|:----|
| java:*uncompress* | Java 1.8.0_161 | buffer | 0.0% | 5,486.7 | 7,006.7 |  |
| java:*uncompress* | Java 1.8.0_161 | stream | 0.0% | 7,216.4 | 5,006.0 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | buffer | -0.005% | 4,217.0 | 6,905.9 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | stream | -0.031% | 3,438.0 | 3,920.2 |  |
| org.lz4:lz4-java | 1.4.1 | buffer | -0.392% | 4,576.8 | 5,992.8 |  |
| org.lz4:lz4-java | 1.4.1 | stream | -0.032% | 1,496.4 | 1,966.1 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | buffer | -0.002% | 1,120.9 | 6,842.5 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | stream | -0.002% | 966.5 | 2,567.9 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | buffer | -0.002% | 1,025.5 | 6,874.5 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | stream | -0.002% | 872.2 | 2,454.1 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | buffer | -0.002% | 211.6 | 6,873.1 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | stream | -0.002% | 188.8 | 2,368.3 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | buffer | -0.002% | 91.8 | 6,925.8 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | stream | -0.002% | 85.5 | 2,481.6 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | buffer | -0.002% | 6.5 | 6,984.4 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | stream | -0.002% | 6.5 | 2,354.8 |  |
| java:zlib | Java 1.8.0_161 | stream | -0.031% | 29.5 | 620.5 |  |
| java:gzip | Java 1.8.0_161 | stream | -0.031% | 31.3 | 801.0 |  |
| org.apache.commons:commons-compress (bzip2) | 1.16.1 | stream | -0.434% | 4.1 | 8.8 |  |

## Norm(μ=0,σ<sup>2</sup>=100) Random `Int[26214400]` Array

datasize=100MB, entropy=1.871

| Name | Version | Method | Rate | Compress [MB/sec] | Decompress [MB/sec] | ERR |
|:-----|:--------|:-------|-----:|------------------:|--------------------:|:----|
| java:*uncompress* | Java 1.8.0_161 | buffer | 0.0% | 7,194.2 | 7,195.0 |  |
| java:*uncompress* | Java 1.8.0_161 | stream | 0.0% | 7,209.3 | 4,837.1 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | buffer | 51.3% | 308.7 | 783.7 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | stream | 51.2% | 286.1 | 647.6 |  |
| org.lz4:lz4-java | 1.4.1 | buffer | 30.2% | 295.0 | 423.6 |  |
| org.lz4:lz4-java | 1.4.1 | stream | 30.0% | 228.5 | 356.4 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | buffer | 71.1% | 205.1 | 503.2 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | stream | 71.1% | 190.9 | 437.0 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | buffer | 72.7% | 161.5 | 514.5 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | stream | 72.7% | 152.2 | 446.0 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | buffer | 73.8% | 70.1 | 541.2 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | stream | 73.8% | 64.7 | 469.7 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | buffer | 74.1% | 39.1 | 571.0 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | stream | 74.1% | 35.9 | 490.5 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | buffer | 78.3% | 2.7 | 734.6 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | stream | 78.3% | 2.7 | 623.0 |  |
| java:zlib | Java 1.8.0_161 | stream | 73.3% | 7.2 | 258.9 |  |
| java:gzip | Java 1.8.0_161 | stream | 73.3% | 7.2 | 272.3 |  |
| org.apache.commons:commons-compress (bzip2) | 1.16.1 | stream | 81.9% | 5.1 | 15.9 |  |

## Norm(μ=0,σ<sup>2</sup>=1) Random `Double[13107200]` Array

datasize=100MB, entropy=5.284

| Name | Version | Method | Rate | Compress [MB/sec] | Decompress [MB/sec] | ERR |
|:-----|:--------|:-------|-----:|------------------:|--------------------:|:----|
| java:*uncompress* | Java 1.8.0_161 | buffer | 0.0% | 7,077.9 | 7,166.4 |  |
| java:*uncompress* | Java 1.8.0_161 | stream | 0.0% | 7,167.8 | 4,947.0 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | buffer | -0.005% | 4,133.8 | 6,975.8 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | stream | -0.031% | 3,417.3 | 3,836.6 |  |
| org.lz4:lz4-java | 1.4.1 | buffer | -0.392% | 4,673.9 | 5,960.5 |  |
| org.lz4:lz4-java | 1.4.1 | stream | -0.032% | 1,740.8 | 2,374.1 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | buffer | 4.16% | 489.5 | 558.1 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | stream | 4.16% | 412.2 | 481.6 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | buffer | 4.16% | 462.3 | 564.5 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | stream | 4.16% | 427.4 | 485.3 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | buffer | 4.16% | 181.6 | 556.2 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | stream | 4.16% | 161.4 | 486.8 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | buffer | 4.16% | 81.6 | 564.0 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | stream | 4.16% | 75.8 | 474.3 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | buffer | 4.15% | 6.2 | 561.1 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | stream | 4.15% | 6.2 | 475.9 |  |
| java:zlib | Java 1.8.0_161 | stream | 3.93% | 20.5 | 151.8 |  |
| java:gzip | Java 1.8.0_161 | stream | 3.93% | 20.5 | 162.8 |  |
| org.apache.commons:commons-compress (bzip2) | 1.16.1 | stream | 3.24% | 4.5 | 8.8 |  |

## Random Floating-Point CSV ASCII Text

datasize=100MB, entropy=2.460

| Name | Version | Method | Rate | Compress [MB/sec] | Decompress [MB/sec] | ERR |
|:-----|:--------|:-------|-----:|------------------:|--------------------:|:----|
| java:*uncompress* | Java 1.8.0_161 | buffer | 0.0% | 7,117.9 | 7,215.0 |  |
| java:*uncompress* | Java 1.8.0_161 | stream | 0.0% | 7,217.0 | 2,407.4 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | buffer | 22.5% | 167.3 | 596.5 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | stream | 20.4% | 161.8 | 471.2 |  |
| org.lz4:lz4-java | 1.4.1 | buffer | 23.4% | 152.7 | 384.4 |  |
| org.lz4:lz4-java | 1.4.1 | stream | 24.6% | 149.9 | 297.4 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | buffer | 55.4% | 184.7 | 671.4 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | stream | 55.4% | 158.6 | 524.3 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | buffer | 52.2% | 78.9 | 359.4 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | stream | 52.2% | 74.1 | 298.0 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | buffer | 54.4% | 38.2 | 354.7 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | stream | 54.4% | 33.6 | 289.4 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | buffer | 54.7% | 9.8 | 368.0 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | stream | 54.7% | 8.6 | 294.9 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | buffer | 59.0% | 1.7 | 607.8 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | stream | 59.0% | 1.7 | 479.6 |  |
| java:zlib | Java 1.8.0_161 | stream | 54.2% | 9.6 | 162.0 |  |
| java:gzip | Java 1.8.0_161 | stream | 54.2% | 9.8 | 166.8 |  |
| org.apache.commons:commons-compress (bzip2) | 1.16.1 | stream | 60.1% | 6.5 | 11.0 |  |

## United States Declaration of Independence, ASCII Text

datasize=0MB, entropy=3.509

| Name | Version | Method | Rate | Compress [MB/sec] | Decompress [MB/sec] | ERR |
|:-----|:--------|:-------|-----:|------------------:|--------------------:|:----|
| java:*uncompress* | Java 1.8.0_161 | buffer | 0.0% | 55,172.6 | 55,784.8 |  |
| java:*uncompress* | Java 1.8.0_161 | stream | 0.0% | 53,351.7 | 3,404.3 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | buffer | 31.8% | 224.3 | 1,274.9 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | stream | 31.5% | 221.3 | 631.3 |  |
| org.lz4:lz4-java | 1.4.1 | buffer | 31.2% | 255.8 | 740.9 |  |
| org.lz4:lz4-java | 1.4.1 | stream | 30.7% | 129.5 | 418.5 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | buffer | 51.8% | 147.6 | 459.3 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | stream | 50.4% | 39.5 | 67.0 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | buffer | 53.0% | 91.3 | 367.0 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | stream | 53.1% | 12.9 | 68.9 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | buffer | 54.7% | 37.2 | 166.2 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | stream | 54.0% | 4.7 | 62.8 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | buffer | 54.8% | 19.7 | 328.0 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | stream | 54.2% | 2.3 | 61.6 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | buffer | 55.2% | 9.2 | 236.4 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | stream | 54.7% | 0.6 | 53.5 |  |
| java:zlib | Java 1.8.0_161 | stream | 54.6% | 26.2 | 113.3 |  |
| java:gzip | Java 1.8.0_161 | stream | 54.5% | 19.0 | 92.0 |  |
| org.apache.commons:commons-compress (bzip2) | 1.16.1 | stream | 58.2% | 1.7 | 10.9 |  |

## 夏目漱石 こころ, Japanese UTF-8 Text

datasize=0MB, entropy=3.194

| Name | Version | Method | Rate | Compress [MB/sec] | Decompress [MB/sec] | ERR |
|:-----|:--------|:-------|-----:|------------------:|--------------------:|:----|
| java:*uncompress* | Java 1.8.0_161 | buffer | 0.0% | 20,566.1 | 19,980.4 |  |
| java:*uncompress* | Java 1.8.0_161 | stream | 0.0% | 20,339.0 | 3,337.9 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | buffer | 47.4% | 182.2 | 897.1 |  |
| org.xerial.snappy:snappy-java | 1.1.7.1 | stream | 45.5% | 177.6 | 631.7 |  |
| org.lz4:lz4-java | 1.4.1 | buffer | 44.9% | 210.0 | 473.8 |  |
| org.lz4:lz4-java | 1.4.1 | stream | 44.1% | 204.9 | 367.2 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | buffer | 64.4% | 174.1 | 617.1 |  |
| com.github.luben:zstd-jni (level=1) | 1.3.3-4 | stream | 64.4% | 145.4 | 412.4 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | buffer | 68.2% | 104.9 | 483.5 |  |
| com.github.luben:zstd-jni (level=3) | 1.3.3-4 | stream | 68.2% | 94.4 | 305.3 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | buffer | 69.8% | 40.7 | 478.3 |  |
| com.github.luben:zstd-jni (level=6) | 1.3.3-4 | stream | 69.8% | 26.4 | 246.7 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | buffer | 71.0% | 22.5 | 570.4 |  |
| com.github.luben:zstd-jni (level=9) | 1.3.3-4 | stream | 71.1% | 19.0 | 290.7 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | buffer | 74.0% | 3.3 | 566.8 |  |
| com.github.luben:zstd-jni (level=16) | 1.3.3-4 | stream | 74.0% | 3.9 | 358.3 |  |
| java:zlib | Java 1.8.0_161 | stream | 68.6% | 11.8 | 190.2 |  |
| java:gzip | Java 1.8.0_161 | stream | 68.6% | 12.0 | 190.3 |  |
| org.apache.commons:commons-compress (bzip2) | 1.16.1 | stream | 78.8% | 8.8 | 18.4 |  |

