# 🎰 Distributed Online Gaming Platform

[cite_start]This repository contains a distributed online gaming system built entirely from scratch[cite: 128]. [cite_start]It was developed as a mandatory assignment for the "Distributed Systems" course (Spring Semester 2025-2026)[cite: 124, 125].

[cite_start]The system relies entirely on raw Java TCP Sockets and features an in-memory data store, a custom MapReduce framework, a Producer-Consumer secure random number generator, and an Android frontend[cite: 143, 148, 201, 221, 231].

## 🏛️ System Architecture

* [cite_start]**Master Node:** A multi-threaded Java TCP server that routes client requests and orchestrates the MapReduce operations[cite: 217, 219]. [cite_start]It assigns games to specific Worker nodes using a modulo hash function: `NodeId = H(GameName) mod NumberOfNodes`[cite: 186, 187].
* [cite_start]**Worker Nodes:** Multi-threaded Java servers that store game metadata entirely in-memory[cite: 188, 222]. [cite_start]The number of active Workers is defined dynamically during the Master node's initialization[cite: 223].
* [cite_start]**Reducer Node:** A multi-threaded Java server responsible for the "Reduce" phase of the framework, merging intermediate values from Workers to produce final query results[cite: 227].
* [cite_start]**Secure Random Generator (SRG):** A separate TCP server utilizing a Producer-Consumer model[cite: 200, 201]. [cite_start]The SRG (Producer) continuously generates random numbers into a queue buffer, while Workers (Consumers) poll this buffer during gameplay[cite: 202, 203]. [cite_start]It ensures secure communication by sending a `sha256(number+secret)` hash alongside the random number[cite: 205].
* [cite_start]**Manager Application (Console):** An interface for platform administrators to add/remove games, update risk levels, and query total profits/losses per game or player[cite: 130, 132, 133, 134].
* [cite_start]**Player Application (Android):** A mobile UI allowing users to asynchronously search for games based on filters (stars, bet limits, risk levels), place bets, and add balance[cite: 137, 143, 144]. [cite_start]It maintains an open TCP connection with the Master until results are received[cite: 275].

## ✨ Key Technical Features

* [cite_start]**Custom MapReduce Implementation:** Used to parallelize game filtering for players and to calculate profit/loss aggregations across distributed Worker nodes[cite: 193, 213].
* [cite_start]**Low-Level Concurrency:** Synchronization is implemented strictly using raw `synchronized`, `wait`, and `notify` mechanisms, bypassing standard `java.util.concurrent` utilities[cite: 230].
* [cite_start]**Risk-Based Game Logic:** Bets are resolved by analyzing the random integer from the SRG[cite: 207]. [cite_start]`number % 100 == 0` triggers a predefined Jackpot, while `number % 10` serves as an index for predefined multiplier arrays based on the game's risk level (Low, Medium, High)[cite: 198, 199, 207, 209].
* [cite_start]**Active Replication (Fault Tolerance):** Implements game data replication across multiple nodes to ensure the system remains highly available and routes requests to backups if a Worker node fails[cite: 278, 280].

## 🛠️ Tech Stack

* [cite_start]**Backend:** Java (strictly using TCP sockets, no HTTP libraries)[cite: 217, 218].
* [cite_start]**Frontend:** Android SDK / Java[cite: 271].
* [cite_start]**Data Storage:** In-Memory Data Structures (Databases are strictly prohibited by project constraints)[cite: 231].

## 🚀 How to Build and Run

1. Clone this repository to your local machine.
2. Compile the Java backend files.
3. [cite_start]Start the **Secure Random Generator (SRG)** server in a new terminal[cite: 220].
4. Start the **Reducer Node** in a new terminal.
5. Start multiple **Worker Nodes** in separate terminals.
6. [cite_start]Start the **Master Node** and pass the number of Workers via configuration or command-line arguments[cite: 223].
7. [cite_start]Run the **Manager Console App** to populate the system with games using JSON metadata files[cite: 160].
8. [cite_start]Build and run the **Android Application** via Android Studio to interact with the platform as a player[cite: 271].
