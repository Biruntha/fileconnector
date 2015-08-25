package org.wso2.carbon.connector;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.Connector;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class FileList extends AbstractConnector implements Connector {

    private static Log log = LogFactory.getLog(FileList.class);

    public void connect(MessageContext messageContext) throws ConnectException {

        String fileLocation =
                getParameter(messageContext, "filelocation") == null ? "" : getParameter(
                        messageContext,
                        "filelocation").toString();


        if (log.isDebugEnabled()) {
            log.info("File Location..." + fileLocation.toString());
        }
        try {
            read(messageContext, fileLocation);
        }catch (IOException e) {
            handleException(e.getMessage(), messageContext);
            log.error(e.getMessage());
        }
        //  generateResults(messageContext,out);
        if (log.isDebugEnabled()) {
            log.info("File read......");
        }
    }

    /**
     *
     * @param messageContext
     * @param fileLocation
     * @throws ZipException
     * @throws IOException
     */
    public void read(MessageContext messageContext,String fileLocation) throws ZipException,IOException {

        log.info("reading a zip file ");

        OMFactory factory = OMAbstractFactory.getOMFactory();
        String outputresult = "";
        int i = 1;

        File file = new File(fileLocation);
        ZipFile zipfile = new ZipFile(file);
        ZipEntry zipentry;
        OMNamespace ns = factory.createOMNamespace(fileLocation, "ns");
        OMElement result = factory.createOMElement("result", ns);
            for (Enumeration<? extends ZipEntry> e = zipfile.entries(); e.hasMoreElements(); ) {
                zipentry = e.nextElement();
                if (!zipentry.isDirectory()) {
                    outputresult = zipentry.getName();
                    OMElement messageElement = factory.createOMElement("file" + i, ns);
                    messageElement.setText(outputresult);
                    result.addChild(messageElement);
                    i++;
                }
            }
            messageContext.getEnvelope().getBody().addChild(result);
    }
}

