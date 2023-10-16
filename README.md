### Analytic Engine  


Intro
=====
To create an implementation to cope with a high load of market data being published (over thousands per second)
and perform CPU intensive analytics calculation. 



Build and Run
=======
To build: mvn clean install  
To Run: ./run.sh and output file to target/out.log  
3rd lib to be used  
- apache.commons commons-pool2 to make message objects pooled  
- mockito for object mocking/stubbing in unit tests  

Input Market Data File:  
path: src/main/resources/marketdata_hk_20230818.csv (HKEx market data on 23230818 of selected stocks)  
sample content:  
40,1,\n1,39,161\n3690,1,132.6|50|D|-|09:00:00|1\n  
40,1,\n1,39,161\n9618,1,137.6|24|D|-|09:00:00|1\n  
43,1,\n1,39,161\n27,1,52.426|130200|P|-|09:00:00|1\n  
42,1,\n1,39,161\n27,2,52.425|86800|P|-|09:00:00|2\n  
45,1,\n1,39,161\n2331,1,40.786|500000|P|-|09:00:00|1\n  

Output of Analytics:  
There are two types of analytics works, i.e. VWAP calculation and Open High Low Close (OHLC) detection  
[VWAPAnalyticsResultImpl--stockCode:700|timestampHHMMSS:15:59:47|vwap:328.70138164808816]  
[OHLCAnalyticsResultImpl--stockCode:700|timestampHHMMSS:15:59:47|open:329.4|high:333.0|low:323.6|close:325.8]  

