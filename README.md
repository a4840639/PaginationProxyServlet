# PaginationProxyServlet
A Java proxy webservice with the following workflow:
1. Receive a SOAP request for a single page
2. Forward the request to an external endpoint
3. Automatically iterating through all the availble pages and getting all the responses from the external endpoint
4. Transforming all the responses with XSLT, then appending all the transformed reponses together
5. Push the appended file to Amazon S3
