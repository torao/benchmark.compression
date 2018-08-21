# JavaVM Compression Libraries Benchmark & Comparison

<div><small><b>Date</b>: 2018/03/23 12:00:27, <b>Author</b>: t_takami @ OKW-721, <b>JavaVM</b>: Java HotSpot(TM) 64-Bit Server VM Oracle Corporation 25.162-b12, <b>Machine Model</b>: DELL OptiPlex 7050, <b>Operating System</b>: Windows 10 Pro, <b>CPU</b>: Intel® Core™ i7-7700 @ 3.60GHz 4 Core 8 Thread, <b>Memory</b>: 16.0GB </small></div>

## Zero-filled `Byte[10485760]` Array

<div><small><b>Block Size</b>: 10.0MB, <b>Block Entropy</b>: 0.0004, <b>Filled By</b>: <code>0</code>, <b>Sample Bytes</b>: <small>00 00 00 00 00 00 00 00 00 00 00 00 00 00 00...</small> </small></div>

| Name | Version | Method | Rate | Compress <small>[MB/sec]</small> | Decompress <small>[MB/sec]</small> |     |
|:-----|:--------|:-------|-----:|---------------------------------:|-----------------------------------:|:----|
| org.meteogroup.jbrotli:jbrotli | 0.5.0 | block | 99.999% | 13.4 | 798.8 | ✔ |

## Uniform Random filled `Byte[10485760]` Array

<div><small><b>Block Size</b>: 10.0MB, <b>Block Entropy</b>: 5.545, <b>Filled By</b>: <code>random.nextByte()</code>, <b>Sample Bytes</b>: <small>22 A9 D8 EE 78 07 C8 EA 8E 79 45 D5 89 EF A4...</small> </small></div>

| Name | Version | Method | Rate | Compress <small>[MB/sec]</small> | Decompress <small>[MB/sec]</small> |     |
|:-----|:--------|:-------|-----:|---------------------------------:|-----------------------------------:|:----|
| org.meteogroup.jbrotli:jbrotli | 0.5.0 | block | -0.0003% | 1.1 | 3,307.3 | ✔ |

## Norm(μ=0,σ<sup>2</sup>=100) Random `Int[2621440]` Array

<div><small><b>Block Size</b>: 10.0MB, <b>Block Entropy</b>: 1.871, <b>Filled By</b>: <code>μ + random.nextGaussian() × σ</code>, <b>Sample Bytes</b>: <small>FF FF FF F9 FF FF FF F5 FF FF FF EC FF FF FF...</small> </small></div>

| Name | Version | Method | Rate | Compress <small>[MB/sec]</small> | Decompress <small>[MB/sec]</small> |     |
|:-----|:--------|:-------|-----:|---------------------------------:|-----------------------------------:|:----|
| org.meteogroup.jbrotli:jbrotli | 0.5.0 | block | 78.5% | 0.4 | 426.2 | ✔ |

## Norm(μ=0,σ<sup>2</sup>=1) Random `Double[1310720]` Array

<div><small><b>Block Size</b>: 10.0MB, <b>Block Entropy</b>: 5.284, <b>Filled By</b>: <code>μ + random.nextGaussian() × σ</code>, <b>Sample Bytes</b>: <small>BF E7 C1 96 29 AB 48 65 BF F2 1A 9C 95 29 D6...</small> </small></div>

| Name | Version | Method | Rate | Compress <small>[MB/sec]</small> | Decompress <small>[MB/sec]</small> |     |
|:-----|:--------|:-------|-----:|---------------------------------:|-----------------------------------:|:----|
| org.meteogroup.jbrotli:jbrotli | 0.5.0 | block | 4.4% | 0.4 | 96.1 | ✔ |

## CSV Formatted Text of Random Floating-Point Values, ASCII Text

<div><small><b>Block Size</b>: 10.0MB, <b>Block Entropy</b>: 2.461, <b>Filled By</b>: <code>0.932993485288541,0.8330913489710237,0.32647575623...</code>, <b>Sample Bytes</b>: <small>30 2E 39 33 32 39 39 33 34 38 35 32 38 38 35...</small> </small></div>

| Name | Version | Method | Rate | Compress <small>[MB/sec]</small> | Decompress <small>[MB/sec]</small> |     |
|:-----|:--------|:-------|-----:|---------------------------------:|-----------------------------------:|:----|
| org.meteogroup.jbrotli:jbrotli | 0.5.0 | block | 58.3% | 0.4 | 132.5 | ✔ |

## United States Declaration of Independence, ASCII Text

<div><small><b>Block Size</b>: 9.4kB, <b>Block Entropy</b>: 3.424, <b>Filled By</b>: <code>IN CONGRESS, JULY 4, 1776.
THE UNANIMOUS
DECLARATI...</code>, <b>Sample Bytes</b>: <small>49 4E 20 43 4F 4E 47 52 45 53 53 2C 20 4A 55...</small> </small></div>

| Name | Version | Method | Rate | Compress <small>[MB/sec]</small> | Decompress <small>[MB/sec]</small> |     |
|:-----|:--------|:-------|-----:|---------------------------------:|-----------------------------------:|:----|
| org.meteogroup.jbrotli:jbrotli | 0.5.0 | block | 63.8% | 0.8 | 192.5 | ✔ |

## 夏目漱石 こころ, Japanese UTF-8 Text

<div><small><b>Block Size</b>: 473.2kB, <b>Block Entropy</b>: 3.182, <b>Filled By</b>: <code>　私はその人を常に先生と呼んでいた。だからここでも...</code>, <b>Sample Bytes</b>: <small>E3 80 80 E7 A7 81 E3 81 AF E3 81 9D E3 81 AE...</small> </small></div>

| Name | Version | Method | Rate | Compress <small>[MB/sec]</small> | Decompress <small>[MB/sec]</small> |     |
|:-----|:--------|:-------|-----:|---------------------------------:|-----------------------------------:|:----|
| org.meteogroup.jbrotli:jbrotli | 0.5.0 | block | 74.6% | 0.5 | 434.2 | ✔ |

