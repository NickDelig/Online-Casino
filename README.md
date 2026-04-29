# 🎰 Distributed Online Gaming Platform

This repository contains a distributed online gaming system built entirely from scratch. It was developed as a mandatory assignment for the "Distributed Systems" course (Spring Semester 2025-2026).

The system relies entirely on raw Java TCP Sockets and features an in-memory data store, a custom MapReduce framework, a Producer-Consumer secure random number generator, and an Android frontend.

## 🏛️ System Architecture

* **Master Node:** A multi-threaded Java TCP server that routes client requests and orchestrates the MapReduce operations. It assigns games to specific Worker nodes using a modulo hash function: `NodeId = H(GameName) mod NumberOfNodes`.
* **Worker Nodes:** Multi-threaded Java servers that store game metadata entirely in-memory. The number of active Workers is defined dynamically during the Master node's initialization.
* **Reducer Node:** A multi-threaded Java server responsible for the "Reduce" phase of the framework, merging intermediate values from Workers to produce final query results.
* **Secure Random Generator (SRG):** A separate TCP server utilizing a Producer-Consumer model. The SRG (Producer) continuously generates random numbers into a queue buffer, while Workers (Consumers) poll this buffer during gameplay. It ensures secure communication by sending a `sha256(number+secret)` hash alongside the random number.
* **Manager Application (Console):** An interface for platform administrators to add/remove games, update risk levels, and query total profits/losses per game or player.
* **Player Application (Android):** A mobile UI allowing users to asynchronously search for games based on filters (stars, bet limits, risk levels), place bets, and add balance. It maintains an open TCP connection with the Master until results are received.

## ✨ Key Technical Features

* **Custom MapReduce Implementation:** Used to parallelize game filtering for players and to calculate profit/loss aggregations across distributed Worker nodes.
* **Low-Level Concurrency:** Synchronization is implemented strictly using raw `synchronized`, `wait`, and `notify` mechanisms, bypassing standard `java.util.concurrent` utilities.
* **Risk-Based Game Logic:** Bets are resolved by analyzing the random integer from the SRG. `number % 100 == 0` triggers a predefined Jackpot, while `number % 10` serves as an index for predefined multiplier arrays based on the game's risk level (Low, Medium, High).
* **Active Replication (Fault Tolerance):** Implements game data replication across multiple nodes to ensure the system remains highly available and routes requests to backups if a Worker node fails.

## 🛠️ Tech Stack

* **Backend:** Java (strictly using TCP sockets, no HTTP libraries).
* **Frontend:** Android SDK / Java.
* **Data Storage:** In-Memory Data Structures (Databases are strictly prohibited by project constraints).

## 🚀 How to Build and Run

1. Clone this repository to your local machine.
2. Compile the Java backend files.
3. Start the **Secure Random Generator (SRG)** server in a new terminal.
4. Start the **Reducer Node** in a new terminal.
5. Start multiple **Worker Nodes** in separate terminals.
6. Start the **Master Node** and pass the number of Workers via configuration or command-line arguments.
7. Run the **Manager Console App** to populate the system with games using JSON metadata files.
8. Build and run the **Android Application** via Android Studio to interact with the platform as a player.
