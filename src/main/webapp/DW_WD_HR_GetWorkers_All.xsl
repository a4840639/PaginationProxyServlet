<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:wd="urn:com.workday/bsvc">
    <xsl:output method="xml" omit-xml-declaration="yes" indent="no"/>
    <xsl:strip-space elements="*"/>    
    <xsl:variable name="linefeed" select="'&#xA;'"></xsl:variable>
    <xsl:variable name="delimitter" select="'|'"></xsl:variable>
    
    <xsl:template match="//wd:Get_Workers_Response">
        <ms11:text xmlns:ms11="http://ws.apache.org/commons/ns/payload">
            <xsl:text>Transaction_Data_Updated_From|Transaction_Data_Updated_Through|Transaction_Data_Effective_From|Transaction_Data_Effective_Through|Worker_WID|Worker_Employee_ID|Worker_User_ID|Legal_Name_Data_First_Name|Legal_Name_Data_Last_Name|Legal_Name_Data_Country_Reference_WID|Legal_Name_Data_ISO_3166 1_Alpha 2_Code|Legal_Name_Data_ISO_3166 1_Alpha 3_Code|Legal_Name_Data_ISO_3166 1_Numeric 3_Code|Preferred_Name_Data_First_Name|Preferred_Name_Data_Last_Name|Preferred_Name_Data_Country_Reference_WID|Preferred_Name_Data_ISO_3166 1_Alpha 2_Code|Preferred_Name_Data_ISO_3166 1_Alpha 3_Code|Preferred_Name_Data_ISO_3166 1_Numeric 3_Code|Gender_Reference_WID|Gender_Code|Birth_Date|Martial_Status_Reference_WID|Martial_Status_Descriptor|Ethnicity_Reference_WID|Ethnicity_ID|Ethnicity_Reference_Descriptor|Hispanic_or_Latino|Citizenship_Status_Reference_ID|Citizenship_Status_Code|Citizenship_Status_Descriptor|National_Data_ID|National_ID_Reference_WID|National_ID_Reference_Descriptor|ID_Type_Reference_WID|National_ID_Type_Code|ID_Type_Reference_Descriptor|Country_Reference_Descriptor|Country_Reference_WID|Country_Reference_ISO_3166 1_Alpha 2_Code|Country_Reference_ISO_3166 1_Alpha 3_Code|Country_Reference_ISO_3166 1_Numeric 3_Code|Country_Reference_Issue_Date|Country_Reference_Verification_Date|Verified_By_Reference_Descriptor|Verified_By_WID|Verified_By_Employee_ID|National_ID_Shared_Reference_Descriptor|National_ID_Shared_Reference_WID|Passport_ID_Data_ID|Passport_ID_Type_Reference_WID|Passport_ID_Type_ID|Passport_Country_Reference_Descriptor|Passport_Country_Reference_WID|Passport_Country_Reference_ISO_3166 1 Alpha 2_Code|Passport_Country_Reference_ISO_3166 1 Alpha 3_Code|Passport_Country_Reference_ISO_3166 1 Numeric 3_Code|Passport_Issued_Date|Passport_Expiration_Date|Passport_Verification Date|Passport_ID_Shared_Reference_WID|Passport_Identifier_Reference_ID|Visa_ID_Data_ID|Visa_ID_Type_Reference_WID|Visa_ID_Type_ID|Visa_Country_Reference_Descriptor|Visa_Country_Reference_WID|Visa_Country_Reference_ISO_3166 1 Alpha 2_Code|Visa_Country_Reference_ISO_3166 1 Alpha 3_Code|Visa_Country_Reference_ISO_3166 1 Numeric 3_Code|Visa_Issued_Date|Visa_Expiration_Date|Visa_Verification Date|Visa_Verified_By_Reference_Descriptor|Visa_Verified_By_WID|Visa_Verified_By_Employee_ID|Visa_ID_Shared_Reference_WID|Visa_Identifier_Reference_ID|License_ID_Reference_Descriptor|License_ID_Reference_WID|License_ID|License_ID_Type_Reference_WID|License_ID_Type_ID|Country_Region_Reference_Descriptor|Country_Region_Reference_WID|Country_Region_ID|Country_Region_ISO_3166 2_Code|Country_Region_Descriptor|License_Issued_Date|License_Verification Date|License_ID_Shared_Reference_Descriptor|License_Identifier_Reference_ID|Custom_ID_Reference_Descriptor|Custom_ID_Reference_WID|Custom_ID_Data_ID|Custom_ID_Type_Reference_Descriptor|Custom_ID_Type_Reference_WID|Custom_ID_Type_ID|Custom_ID_Shared_Reference_WID|Custom_Identifier_Reference_ID|Address_Data_Effective_Date_1|Address_Data_Last_Modified_Date_1|Address_Line_Data_1|Municipality_1|Country_Region_Reference_WID_1|Country_Region_ID_1|ISO_3166_2_Code_1|Country_Region_Descriptor_1|Postal_Code_1|Usage_Type_Reference_WID_1|Communications_Usage_Type_ID_1|Address_Data_Effective_Date_2|Address_Data_Last_Modified_Date_2|Address_Line_Data_2|Municipality_2|Country_Region_Reference_WID_2|Country_Region_ID_2|ISO_3166_2_Code_2|Country_Region_Descriptor_2|Postal_Code_2|Usage_Type_Reference_WID_2|Communications_Usage_Type_ID_2|Address_Data_Effective_Date_3|Address_Data_Last_Modified_Date_3|Address_Line_Data_3|Municipality_3|Country_Region_Reference_WID_3|Country_Region_ID_3|ISO_3166_2_Code_3|Country_Region_Descriptor_3|Postal_Code_3|Usage_Type_Reference_WID_3|Communications_Usage_Type_ID_3|Phone_Data_Country_ISO_Code_1|International_Phone_Code_1|Area_Code_1|Phone_Number_1|Phone_Device_Type_WID_1|Phone_Device_Type_ID_1|Phone_Device_Type_Descriptor_1|Usage_Data_Descriptor_1|Usage_Data_Primary_1|Usage_Data_WID_1|Communication_Usage_Type_ID_1|Phone_Data_Country_ISO_Code_2|International_Phone_Code_2|Area_Code_2|Phone_Number_2|Phone_Device_Type_WID_2|Phone_Device_Type_ID_2|Phone_Device_Type_Descriptor_2|Usage_Data_Descriptor_2|Usage_Data_Primary_2|Usage_Data_WID_2|Communication_Usage_Type_ID_2|Phone_Data_Country_ISO_Code_3|International_Phone_Code_3|Area_Code_3|Phone_Number_3|Phone_Device_Type_WID_3|Phone_Device_Type_ID_3|Phone_Device_Type_Descriptor_3|Usage_Data_Descriptor_3|Usage_Data_Primary_3|Usage_Data_WID_3|Communication_Usage_Type_ID_3|Email_Address_1|Email_Usage_Data_Type_Descriptor_1|Email_Usage_Data_Primary_1|Email_Usage_Data_WID_1|Email_Communication_Usage_Type_ID_1|Email_Address_2|Email_Usage_Data_Type_Descriptor_2|Email_Usage_Data_Primary_2|Email_Usage_Data_WID_2|Email_Communication_Usage_Type_ID_2|Instant_Message_Address_1|Instant_Message_Type_Descriptor_1|Instant_Message_Type_Data_WID_1|Instant_Message_Type_Type_ID_1|Instant_Message_Usage_Type_Data_Primary_1|Instant_Message_Usage_Type_Reference_Descriptor_1|Instant_Message_TUsage_Type_WID_1|Instant_Message_Communication_Usage_Type_ID_1|Instant_Message_Address_2|Instant_Message_Type_Descriptor_2|Instant_Message_Type_Data_WID_2|Instant_Message_Type_Type_ID_2|Instant_Message_Usage_Type_Data_Primary_2|Instant_Message_Usage_Type_Reference_Descriptor_2|Instant_Message_TUsage_Type_WID_2|Instant_Message_Communication_Usage_Type_ID_2|Tobacco_Use</xsl:text>
            <xsl:value-of select="$linefeed"/>
            <xsl:apply-templates select="wd:Response_Data/wd:Worker"/> 
        </ms11:text>
    </xsl:template>
    
    <xsl:template name="RequestTransactionInfo">
        <xsl:value-of select="../../wd:Request_Criteria/wd:Transaction_Log_Criteria_Data/wd:Transaction_Date_Range_Data/wd:Updated_From"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="../../wd:Request_Criteria/wd:Transaction_Log_Criteria_Data/wd:Transaction_Date_Range_Data/wd:Updated_Through"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="../../wd:Request_Criteria/wd:Transaction_Log_Criteria_Data/wd:Transaction_Date_Range_Data/wd:Effective_From"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="../../wd:Request_Criteria/wd:Transaction_Log_Criteria_Data/wd:Transaction_Date_Range_Data/wd:Effective_Through"/>
        <xsl:text>|</xsl:text>
    </xsl:template>
    
    <xsl:template match="wd:Worker">
        <xsl:call-template name="RequestTransactionInfo"/>
        <xsl:value-of select="wd:Worker_Reference/wd:ID[@wd:type='WID']"/>
     <!--   <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Worker_Reference/wd:ID[@wd:type='Employee_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Worker_Descriptor"/>  -->
        <xsl:text>|</xsl:text>
        <xsl:apply-templates select="wd:Worker_Data"/>
    </xsl:template>
    
    <xsl:template match="wd:Worker_Data">
        <xsl:value-of select="wd:Worker_ID"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:User_ID"/>
        <xsl:text>|</xsl:text> 
        <xsl:apply-templates select="wd:Personal_Data"/>
    </xsl:template>
    
    <xsl:template match="wd:Personal_Data">
        <xsl:value-of select="wd:Name_Data/wd:Legal_Name_Data/wd:Name_Detail_Data/wd:First_Name"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Name_Data/wd:Legal_Name_Data/wd:Name_Detail_Data/wd:Last_Name"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Name_Data/wd:Legal_Name_Data/wd:Name_Detail_Data/wd:Country_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Name_Data/wd:Legal_Name_Data/wd:Name_Detail_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Alpha-2_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Name_Data/wd:Legal_Name_Data/wd:Name_Detail_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Alpha-3_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Name_Data/wd:Legal_Name_Data/wd:Name_Detail_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Numeric-3_Code']"/>
        <xsl:text>|</xsl:text> 
        <xsl:value-of select="wd:Name_Data/wd:Preferred_Name_Data/wd:Name_Detail_Data/wd:First_Name"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Name_Data/wd:Preferred_Name_Data/wd:Name_Detail_Data/wd:Last_Name"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Name_Data/wd:Preferred_Name_Data/wd:Name_Detail_Data/wd:Country_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Name_Data/wd:Preferred_Name_Data/wd:Name_Detail_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Alpha-2_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Name_Data/wd:Preferred_Name_Data/wd:Name_Detail_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Alpha-3_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Name_Data/wd:Preferred_Name_Data/wd:Name_Detail_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Numeric-3_Code']"/>
        <xsl:text>|</xsl:text> 
        <xsl:value-of select="wd:Gender_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text> 
        <xsl:value-of select="wd:Gender_Reference/wd:ID[@wd:type='Gender_Code']"/>
        <xsl:text>|</xsl:text> 
        <xsl:value-of select="wd:Birth_Date"/>
        <xsl:text>|</xsl:text> 
        <xsl:value-of select="wd:Marital_Status_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Marital_Status_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Ethnicity_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Ethnicity_Reference/wd:ID[@wd:type='Ethnicity_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Ethnicity_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Hispanic_or_Latino"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Citizenship_Status_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Citizenship_Status_Reference/wd:ID[@wd:type='Citizenship_Status_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Citizenship_Status_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:ID"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:ID_Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:ID_Type_Reference/wd:ID[@wd:type='National_ID_Type_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:ID_Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:Country_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:Country_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Alpha-2_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Alpha-3_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Numeric-3_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:Issued_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:Verification_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:Verified_By_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:Verified_By_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Data/wd:Verified_By_Reference/wd:ID[@wd:type='Employee_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Shared_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:National_ID[1]/wd:National_ID_Shared_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Data/wd:ID"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Data/wd:ID_Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Data/wd:ID_Type_Reference/wd:ID[@wd:type='Passport_ID_Type_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Data/wd:Country_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Data/wd:Country_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Alpha-2_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Alpha-3_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Numeric-3_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Data/wd:Issued_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Data/wd:Expiration_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Data/wd:Verification_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Shared_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Passport_ID/wd:Passport_ID_Shared_Reference/wd:ID[@wd:type='Passport_Identifier_Reference_ID']"/>
        <xsl:text>|</xsl:text>
        
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:ID"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:ID_Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:ID_Type_Reference/wd:ID[@wd:type='Visa_ID_Type_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:Country_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:Country_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Alpha-2_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Alpha-3_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:Country_Reference/wd:ID[@wd:type='ISO_3166-1_Numeric-3_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:Issued_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:Expiration_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:Verification_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:Verified_By_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:Verified_By_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Data/wd:Verified_By_Reference/wd:ID[@wd:type='Employee_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Shared_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Visa_ID/wd:Visa_ID_Shared_Reference/wd:ID[@wd:type='Passport_Identifier_Reference_ID']"/>
        <xsl:text>|</xsl:text>
        
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text> 
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Data/wd:ID"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Data/wd:ID_Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Data/wd:ID_Type_Reference/wd:ID[@wd:type='License_ID_Type_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Data/wd:Country_Region_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Data/wd:Country_Region_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Data/wd:Country_Region_Reference/wd:ID[@wd:type='Country_Region_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Data/wd:Country_Region_Reference/wd:ID[@wd:type='ISO_3166-2_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Data/wd:Country_Region_Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Data/wd:Issued_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Data/wd:Verification_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Shared_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:License_ID/wd:License_ID_Shared_Reference/wd:ID[@wd:type='License_Identifier_Reference_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Custom_ID/wd:Custom_ID_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Custom_ID/wd:Custom_ID_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text> 
        <xsl:value-of select="wd:Identification_Data/wd:Custom_ID/wd:Custom_ID_Data/wd:ID"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Custom_ID/wd:Custom_ID_Data/wd:ID_Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Custom_ID/wd:Custom_ID_Data/wd:ID_Type_Reference/wd:ID[@wd:type='Custom_ID_Type_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Custom_ID/wd:Custom_ID_Data/wd:ID_Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Custom_ID/wd:Custom_ID_Shared_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Identification_Data/wd:Custom_ID/wd:Custom_ID_Shared_Reference/wd:ID[@wd:type='Custom_Identifier_Reference_ID']"/>
        <xsl:text>|</xsl:text>  
        
        <!-- Contact Info -->
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[1]/@wd:Effective_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[1]/wd:Last_Modified"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[1]/wd:Address_Line_Data"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[1]/wd:Municipality"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[1]/wd:Country_Region_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[1]/wd:Country_Region_Reference/wd:ID[@wd:type='Country_Region_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[1]/wd:Country_Region_Reference/wd:ID[@wd:type='ISO_3166-2_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[1]/wd:Country_Region_Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[1]/wd:Postal_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[1]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[1]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='Communication_Usage_Type_ID']"/>
        <xsl:text>|</xsl:text> 
        
        <!-- Contact Info 2 -->        
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[2]/@wd:Effective_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[2]/wd:Last_Modified"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[2]/wd:Address_Line_Data"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[2]/wd:Municipality"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[2]/wd:Country_Region_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[2]/wd:Country_Region_Reference/wd:ID[@wd:type='Country_Region_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[2]/wd:Country_Region_Reference/wd:ID[@wd:type='ISO_3166-2_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[2]/wd:Country_Region_Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[2]/wd:Postal_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[2]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[2]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='Communication_Usage_Type_ID']"/>
        <xsl:text>|</xsl:text>  
        <!-- Contact Info 3 -->        
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[3]/@wd:Effective_Date"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[3]/wd:Last_Modified"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[3]/wd:Address_Line_Data"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[3]/wd:Municipality"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[3]/wd:Country_Region_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[3]/wd:Country_Region_Reference/wd:ID[@wd:type='Country_Region_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[3]/wd:Country_Region_Reference/wd:ID[@wd:type='ISO_3166-2_Code']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[3]/wd:Country_Region_Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[3]/wd:Postal_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[3]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Address_Data[3]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='Communication_Usage_Type_ID']"/>
        <xsl:text>|</xsl:text>  
        
        <!-- Phone Info 1 -->        
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[1]/wd:Country_ISO_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[1]/wd:International_Phone_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[1]/wd:Area_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[1]/wd:Phone_Number"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[1]/wd:Phone_Device_Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[1]/wd:Phone_Device_Type_Reference/wd:ID[@wd:type='Phone_Device_Type_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[1]/wd:Phone_Device_Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[1]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[1]/wd:Usage_Data/wd:Type_Data/@wd:Primary"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[1]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[1]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='Communication_Usage_Type_ID']"/>
        <xsl:text>|</xsl:text>
        
        <!-- Phone Info 2 -->        
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[2]/wd:Country_ISO_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[2]/wd:International_Phone_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[2]/wd:Area_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[2]/wd:Phone_Number"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[2]/wd:Phone_Device_Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[2]/wd:Phone_Device_Type_Reference/wd:ID[@wd:type='Phone_Device_Type_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[2]/wd:Phone_Device_Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[2]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[2]/wd:Usage_Data/wd:Type_Data/@wd:Primary"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[2]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[2]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='Communication_Usage_Type_ID']"/>
        <xsl:text>|</xsl:text>
        
        <!-- Phone Info 3 -->        
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[3]/wd:Country_ISO_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[3]/wd:International_Phone_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[3]/wd:Area_Code"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[3]/wd:Phone_Number"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[3]/wd:Phone_Device_Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[3]/wd:Phone_Device_Type_Reference/wd:ID[@wd:type='Phone_Device_Type_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[3]/wd:Phone_Device_Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[3]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[3]/wd:Usage_Data/wd:Type_Data/@wd:Primary"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[3]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Phone_Data[3]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='Communication_Usage_Type_ID']"/>
        <xsl:text>|</xsl:text>
        
        <!-- Email 1-->
        <xsl:value-of select="wd:Contact_Data/wd:Email_Address_Data[1]/wd:Email_Address"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Email_Address_Data[1]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Email_Address_Data[1]/wd:Usage_Data/wd:Type_Data/@wd:Primary"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Email_Address_Data[1]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Email_Address_Data[1]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='Communication_Usage_Type_ID']"/>
        <xsl:text>|</xsl:text>
        
        <!-- Email 2-->
        <xsl:value-of select="wd:Contact_Data/wd:Email_Address_Data[2]/wd:Email_Address"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Email_Address_Data[2]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Email_Address_Data[2]/wd:Usage_Data/wd:Type_Data/@wd:Primary"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Email_Address_Data[2]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Email_Address_Data[2]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='Communication_Usage_Type_ID']"/>
        <xsl:text>|</xsl:text>
        
        <!-- Instant Messenger 1-->
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[1]/wd:Instant_Messenger_Address"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[1]/wd:Instant_Messenger_Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[1]/wd:Instant_Messenger_Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[1]/wd:Instant_Messenger_Type_Reference/wd:ID[@wd:type='Instant_Messenger_Type_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[1]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[1]/wd:Usage_Data/wd:Type_Data/@wd:Primary"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[1]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[1]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='Communication_Usage_Type_ID']"/>
        <xsl:text>|</xsl:text>
        
        <!-- Instant Messenger 2-->
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[2]/wd:Instant_Messenger_Address"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[2]/wd:Instant_Messenger_Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[2]/wd:Instant_Messenger_Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[2]/wd:Instant_Messenger_Type_Reference/wd:ID[@wd:type='Instant_Messenger_Type_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[2]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/@wd:Descriptor"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[2]/wd:Usage_Data/wd:Type_Data/@wd:Primary"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[2]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='WID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Contact_Data/wd:Instant_Messenger_Data[2]/wd:Usage_Data/wd:Type_Data/wd:Type_Reference/wd:ID[@wd:type='Communication_Usage_Type_ID']"/>
        <xsl:text>|</xsl:text>
        <xsl:value-of select="wd:Tobacco_Use"/>
        <xsl:value-of select="$linefeed"/>
    </xsl:template>
  </xsl:stylesheet>