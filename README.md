# Connect To IBM MQ Queue

## Overview

This Advanced Action is used to connect to a IBM WebSphere MQ queue. It can be used with NeoLoad JMS Advanced actions Queue – Send, Queue – Receive and Disconnect.

Supported JMS API versions: 1.1 and 2.0.

Important: NeoLoad does not include MQ implementation jars (like mqcontext.jar). These jars, which can be found in the WebSphere Application Server installation directory (for example: C:\Program Files\IBM\WebSphere MQ\java\lib), need to be put in the extlib directory of your NeoLoad installation.


| Property | Value |
| ---------| -------|
| Maturity | Stable |
| Author   | Neotys |
| License  | [BSD Simplified](https://www.neotys.com/documents/legal/bsd-neotys.txt) |
| NeoLoad  | From version 6.3|
| Requirements | <ul><li>License FREE edition, or Enterprise edition, or Professional with Integration & Advanced Usage</li><li>MQ implementation jars</li></ul>|
| Bundled in NeoLoad | No |
| Download Binaries    | See the [latest release](https://github.com/Neotys-Labs/Connect-To-IBM-MQ-Queue/releases/latest)|

## Parameters

| Name                     | Description       | Required/Optional|
| ---------------          | ----------------- |----------------- |
| QueueManager | The fully qualified class name of the factory class that will create the initial context. | required |
| HostName     | The name of the host. | required |
| Port         | The port for client connection. | required |
| Channel      | The name of the channel – Only applies to client transport mode. | required |
| QueueName    | The name of the queue | required |
| QueueSessionTransacted      | A boolean to indicate whether the session is transacted. | optional |
| QueueSessionAcknowledgeMode | An integer to indicate whether the consumer or the client will acknowledge any messages it receives; ignored if the session is transacted. Legal values are 1 for AUTO_ACKNOWLEDGE, 2 for CLIENT_ACKNOWLEDGE or 3 for DUPS_OK_ACKNOWLEDGE.| optional |
| Username     | The username used to create the connection. | optional |
| Password     | The password used to create the connection. | optional |

## Status Codes

NL-CONNECTTOMQQUEUE-ACTION-01: There was an issue parsing the parameters.
NL-CONNECTTOMQQUEUE-ACTION-02: There was an error while creating the connection to the queue.

## Examples

An example of connecting to a IBM WebSphere MQ queue:

QueueManager: myQueueManager
HostName: 10.0.0.52
Port: 1414
Channel: myChannel
QueueName: myQueueName
An example of connecting to a IBM WebSphere MQ queue with no JMS headers.

QueueManager: myQueueManager
HostName: 10.0.0.52
Port: 1414
Channel: myChannel
QueueName: queue:///myQueueName?targetClient=1

## Changelog

* 2.0.3 – Add the possibility to specify the username and password used to create the connection.
