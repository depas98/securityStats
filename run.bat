echo off
javac -d ./bin ./src/com/depas98/security/SecurityMonitor.java ./src/com/depas98/security/SecurityMonitorService.java ./src/com/depas98/security/SecurityMonitorServiceImpl.java ./src/com/depas98/security/SecurityFileReaderTask.java  ./src/com/depas98/security/SecurityStatsWriterTask.java  ./src/com/depas98/security/SecurityType.java

java -cp ./bin com.depas98.security.SecurityMonitor