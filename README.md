# PaginationProxyServlet
A Java proxy webservice with the following workflow:
1. Receive a SOAP request which can be paginated
2. Forward the request to an external endpoint
3. Automatically iterating through all the available pages and getting all the responses from the external endpoint
4. Transforming all the responses with XSLT, then appending all the transformed responses together
5. Push the appended file to Amazon S3
