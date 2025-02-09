# Simple File Transfer System  

A  **client-server file transfer system** using Java **sockets** and **multi-threading**. The server handles multiple clients and allows them to **list available text files** and **upload new files**.

## Features  

- Multi-threaded **server** using a fixed **thread pool**  
- **Client** can:  
  - Request a **list** of files from the server  
  - **Upload** a new file to the server  
- **Server logs** all client requests (list/upload) with timestamps  
- Uses **TCP sockets** for communication  

## Tech Stack  

- **Java**  
- **Sockets** (TCP)  
- **Multi-threading** (Executor service)  

## How to Run  

### 1. Compile the Code  

```bash
cd cwk/server
javac *.java
cd ../client
javac *.java
```

### 2. Start the Server  

```bash
cd ../server
java Server
```

### 3. Run Client Commands  

List files on the server:  
```bash
cd ../client
java Client list
```

Upload a file to the server:  
```bash
java Client put lipsum2.txt
```

## Example Output  

```
> java Client list
Listing 1 file(s):
lipsum1.txt

> java Client put lipsum2.txt
Uploaded file lipsum2.txt

> java Client list
Listing 2 file(s):
lipsum1.txt
lipsum2.txt

> java Client put lipsum2.txt
Error: Cannot upload file 'lipsum2.txt'; already exists on server.
```

