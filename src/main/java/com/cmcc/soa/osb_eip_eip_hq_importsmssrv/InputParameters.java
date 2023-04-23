package com.cmcc.soa.osb_eip_eip_hq_importsmssrv;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "InputParameters", namespace = "http://soa.cmcc.com/OSB_EIP_EIP_HQ_ImportSmsSrv")
@XmlAccessorType(XmlAccessType.FIELD)
public class InputParameters {

    @XmlElement(name = "MSGHEADER")
    private MSGHEADER msgHeader;

    public void setMsgHeader(MSGHEADER msgHeader) {
        this.msgHeader = msgHeader;
    }

    public MSGHEADER getMsgHeader() {
        return msgHeader;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class MSGHEADER {
        @XmlElement(name = "SOURCESYSTEMID")
        private String sourceSystemID;

        @XmlElement(name = "TOKEN")
        private String token;

        @XmlElement(name = "PROVINCE_CODE")
        private String provinceCode;
    }

    @XmlElement(name = "INPUTCOLLECTION")
    private InputCollection inputCollection;

    public InputCollection getInputCollection() {
        return inputCollection;
    }

    public void setInputCollection(InputCollection inputCollection) {
        this.inputCollection = inputCollection;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class InputCollection {
        @XmlElement(name = "INPUTCOLLECTION_ITEM")
        private InputCollectionItem[] inputCollectionItems;

        public InputCollectionItem[] getInputCollectionItems() {
            return inputCollectionItems;
        }

        public void setInputCollectionItems(InputCollectionItem[] inputCollectionItems) {
            this.inputCollectionItems = inputCollectionItems;
        }
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class InputCollectionItem {
        @XmlElement(name = "PRI_KEY")
        private String priKey;

        @XmlElement(name = "INTERNAL_USER_FLAG")
        private String internalUserFlag;

        @XmlElement(name = "USER_ACCOUNT")
        private String userAccount;

        @XmlElement(name = "RECEIVER_NUMBER")
        private String receiverNumber;

        @XmlElement(name = "MESSAGE_CONTENT")
        private String messageContent;

        @XmlElement(name = "FORCE_FLAG")
        private Integer forceFlag;

        @XmlElement(name = "MERGE_SEND_FLAG")
        private Integer mergeSendFlag;

        @XmlElement(name = "APPID")
        private String appid;

        @XmlElement(name = "TEMPLATE_NUM")
        private String templateNum;

        @XmlElement(name = "INPUT_EXT")
        private String inputExt;



    }
}