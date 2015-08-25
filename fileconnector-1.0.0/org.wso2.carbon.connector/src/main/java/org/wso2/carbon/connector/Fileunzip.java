package org.wso2.carbon.connector;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.codehaus.jettison.json.JSONException;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import org.wso2.carbon.connector.util.FileUnzipUtil;
import org.wso2.carbon.connector.util.ResultPayloadCreater;



public class Fileunzip extends AbstractConnector implements Connector {

    private static Log log = LogFactory.getLog(Fileunzip.class);

    public void connect(MessageContext messageContext) throws ConnectException  {

        String fileLocation =
                getParameter(messageContext, "filelocation") == null ? "" : getParameter(
                        messageContext,
                        "filelocation").toString();

        String newFileLocation =
                getParameter(messageContext, "newfilelocation") == null ? "" : getParameter(
                        messageContext,
                        "newfilelocation").toString();
        String archiveType =
                getParameter(messageContext, "archivetype") == null ? "" : getParameter(
                        messageContext,
                        "archivetype").toString();



        if (log.isDebugEnabled()) {
            log.info("File Location..." + fileLocation.toString());
        }
        boolean resultStatus = false;

       try {
           new FileUnzipUtil().unzip(fileLocation, newFileLocation);
           resultStatus = true;
       } catch(IOException e) {
           handleException(e.getMessage(), messageContext);
           log.error(e.getMessage());
           resultStatus = false;
       }
        generateResults(messageContext, resultStatus);
        if (log.isDebugEnabled()) {
            log.info("File extracted......");
        }

    }

    private void generateResults(MessageContext messageContext, boolean resultStatus) {
        ResultPayloadCreater resultPayload = new ResultPayloadCreater();

        String responce = "<result><success>" + resultStatus + "</success></result>";

        try {
            OMElement element = resultPayload.performSearchMessages(responce);
            resultPayload.preparePayload(messageContext, element);

        } catch (XMLStreamException e) {
            log.error(e.getMessage());
            handleException(e.getMessage(), messageContext);
        } catch (IOException e) {
            log.error(e.getMessage());
            handleException(e.getMessage(), messageContext);
        } catch (JSONException e) {
            log.error(e.getMessage());
            handleException(e.getMessage(), messageContext);
        }

    }
}


