# PaginationProxyServlet
A Java proxy webservice with the following workflow:
1. Receive a SOAP request for a single page
2. Forward the request to an external endpoint
3. Automatically iterating through all the availble pages and getting all the responses from the external endpoint
4. Transforming all the responses with XSLT, then appending all the transformed reponses together
5. Push the appended file to Amazon S3

Currently, there are five active branches:
1. master branch: it will concatinate files locally and it respects page order.
2. MT2 branch: also concatinates locally. However, instead of buffering all the separate pages to local disk, it directly appends the transformed data and it does not respect page order.
3. Firehose branch: instead of concatingnating locally, it utilizes Amazon Firehose delivery stream for concatinating. It does repspect page order.
4. Firehose_MT branch: multi-threading version of Firehose branch, does not respect page order.
5. EXT branch: based on master branch. Instead of loading configuration from the configuration file, it loads configuration from the SOAP request.
