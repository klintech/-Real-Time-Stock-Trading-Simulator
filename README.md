# Real-Time Stock Trading Simulator

A Java-based CLI stock trading simulator with real-time order matching, portfolio management, and trade persistence.

## Features
- Place buy/sell orders (uy aapl 10 151.00).
- Track portfolio cash and holdings (portfolio).
- Cancel orders (cancel AAPL-uuid).
- Dynamic pricing with manual (updateprice) and batch updates (atchupdate).
- Trade logs in CSV (	rades.csv).
- Thread-safe order matching with PriorityQueue.

## Setup
1. Clone:
   `ash
   git clone https://github.com/klintech/-Real-Time-Stock-Trading-Simulator.git
   cd -Real-Time-Stock-Trading-Simulator
   ``n2. Compile:
   `ash
   javac -d bin src/engine/*.java src/model/*.java src/util/*.java src/Main.java
   ``n3. Run:
   `ash
   java -cp bin Main
   ``n
## Commands
- uy <symbol> <quantity> <price>
- sell <symbol> <quantity> <price>
- portfolio
- cancel <orderId>
- updateprice <symbol> <price>
- atchupdate <symbol> <changes>
- eport
- stocks
- quit

## License
MIT License
