<p align="center"><img src="/screenshots/logo-ibmmq.png" width="40%" alt="AMQP Logo" /></p>

# Connect To IBM MQ Queue
IBM MQ is messaging middleware that simplifies and accelerates the integration of diverse applications and business data across multiple platforms. It uses message queues to facilitate the exchanges of information and offers a single messaging solution for cloud, mobile, Internet of Things (IoT) and on-premises environments.

By connecting virtually everything from a simple pair of applications to the most complex business environments, IBM MQ helps you improve business responsiveness, control costs, reduce risk—and gain real-time insight from mobile, IoT and sensor data.

## Overview

This Advanced Action is used to connect to a IBM WebSphere MQ queue. It can be used with NeoLoad JMS Advanced actions Queue – Send, Queue – Receive and Disconnect.

Supported JMS API versions: 1.1 and 2.0.

Important: NeoLoad does not include MQ implementation jars (like mqcontext.jar). These jars, which can be found in the WebSphere Application Server installation directory (for example: C:\Program Files\IBM\WebSphere MQ\java\lib), need to be put in the extlib directory of your NeoLoad installation.


| Property | Value |
| ---------| -------|
| Maturity | Experimental |
| Author   | Neotys |
| License  | [BSD Simplified](https://www.neotys.com/documents/legal/bsd-neotys.txt) |
| NeoLoad            | 6.5.1 (Enterprise or Professional Edition w/ Integration & Advanced Usage)|
| Bundled in NeoLoad | No |
| Download Binaries    | See the [latest release](https://github.com/Neotys-Labs/Connect-To-IBM-MQ-Queue/releases/latest)|

## Installation

1. Download the [latest release](https://github.com/Neotys-Labs/Connect-To-IBM-MQ-Queue/releases/latest)
1. Read the NeoLoad documentation to see [How to install a custom Advanced Action](https://www.neotys.com/documents/doc/neoload/latest/en/html/#25928.htm)


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
| CCSID     | Choosing client or server coded character set identifier. | optional |
| TransportProperty     | Tansport property. For example 'MQSeries', 'MQSeries Client', 'MQSeries Bindings', or 'MQJD'. | optional |
| Debug     | Debug: true/fals | optional |
| SslFipsRequired     | Specify that an SSL or TLS channel must use only FIPS-certified CipherSpecs. true/false | optional |
| UseIBMCipherMappings     | UseIBMCipherMappingss. true/false | optional |
| PreferTLS     | PreferTLS. true/false | optional |
| SslCipherSuite     | SSL Cipher Suite. | optional |
| SslPeerName     | SSL Peer Name. | optional |
| SslProtocol     | SSL protocol to use, e.g. TLSv1, TLSv1.2. | optional |

## Examples

An example of connecting to a IBM WebSphere MQ queue:

<p align="center"><img src="/screenshots/connect.png" alt="Connect" /></p>

QueueManager: QM1
HostName: 10.0.1.13
Port: 1414
Channel: DEV.APP.SVRCONN
QueueName: myQueue
Debug: true

## Status Codes:
NL-CONNECTTOMQQUEUE-ACTION-01: There was an issue parsing the parameters.

NL-CONNECTTOMQQUEUE-ACTION-02: There was an error while creating the connection to the queue.


## TLS Support

The advanced action support TLS to encrypt the communication between the client and the IBM server.

To enable TLS on the Connect advanced action, add parameter **PreferTLS** with value true, and parameter **SslProtocol** with the SSL protocol you want to use (for example **TLSv1**, **TLSv1.2**).

Client and server authentication (a.k.a. peer verification) is also supported. The Connect advanced action can exchange signed certificates between the end points of the channel, and those certificates can optionally be verified. The verification of a certificate requires establishing a chain of trust from a known, trusted root certificate, and the certificate presented. 

To use a X509 client certificate to negotiate the TLS communication, import the certificate in PKCS#12 format in the certificate manager of the NeoLoad project settings. See [NeoLoad documentation](https://www.neotys.com/documents/doc/neoload/latest/en/html/#699.htm) for more details.

<p align="center"><img src="/screenshots/certificate-manager.png" alt="Certificate manager" /></p>

## Test environment

This advanced action was tested based on a [docker image of IBM MQ](https://hub.docker.com/r/ibmcom/mq/) version 9.1.2.0. See [docker-compose.yaml](src/test/resources/docker-compose-ibm-mq.yaml) file.

## Changelog

* 2.1.5 - Support of TLS
* 2.0.3 - Add the possibility to specify the username and password used to create the connection.
