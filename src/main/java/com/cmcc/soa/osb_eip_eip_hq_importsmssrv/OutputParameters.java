package com.cmcc.soa.osb_eip_eip_hq_importsmssrv;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author YunTao.Li
 * @description:
 * @date 2023/4/21 14:45
 */
@Data
@XmlRootElement(name = "OutputParameters", namespace = "http://soa.cmcc.com/OSB_EIP_EIP_HQ_ImportSmsSrv")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutputParameters {


    @XmlElement(name = "ESB_FLAG")
    private String esbFlag;

    @XmlElement(name = "ESB_RETURN_CODE")
    private String esbReturnCode;

    @XmlElement(name = "ESB_RETURN_MESSAGE")
    private String esbReturnMessage;

    @XmlElement(name = "BIZ_SERVICE_FLAG")
    private String bizServiceFlag;

    @XmlElement(name = "BIZ_RETURN_CODE")
    private String bizReturnCode;

    @XmlElement(name = "BIZ_RETURN_MESSAGE")
    private String bizReturnMessage;

    @XmlElement(name = "INSTANCE_ID")
    private String instanceID;

    @XmlElement(name = "ERRORCOLLECTION")
    private OutputParameters.ERRORCOLLECTION errorCollection;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ERRORCOLLECTION {

        @XmlElement(name = "ERRORCOLLECTION_ITEM")
        private ERRORCOLLECTION_ITEM[] errorcollectionItems;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ERRORCOLLECTION_ITEM {
        @XmlElement(name = "ERROR_CODE")
        private String errorCode;

        @XmlElement(name = "ERROR_NAME")
        private String errorName;

        @XmlElement(name = "ERROR_MESSAGE")
        private String errorMessage;

        @XmlElement(name = "PRI_KEY")
        private String priKey;

        @XmlElement(name = "ENTITY_NAME")
        private String entityName;

        @XmlElement(name = "RESERVED_1")
        private String reserved1;

        @XmlElement(name = "RESERVED_2")
        private String reserved2;
    }


    @XmlElement(name = "RESPONSECOLLECTION")
    private OutputParameters.RESPONSECOLLECTION responseCollection;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RESPONSECOLLECTION{

        @XmlElement(name = "RESPONSECOLLECTION_ITEM")
        private RESPONSECOLLECTION_ITEM[] responsecollectionItems;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RESPONSECOLLECTION_ITEM{

        @XmlElement(name = "REQUEST_ID")
        private String requestID;

        @XmlElement(name = "PRI_KEY")
        private String priKey;

        @XmlElement(name = "RESP_EXT")
        private String respExt;
    }


}
