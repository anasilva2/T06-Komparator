package org.komparator.security.handler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CertUtil;
import org.komparator.security.CryptoUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

public class SignatureCheckHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String CONTEXT_PROPERTY = "my.property";
	
	private final String caURL = "http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca";
	
	public static final String certificateCA = "/ca.cer";
	
	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if(!outbound.booleanValue())
			verifySignature(smc);
		return true;
	}

	
	private void verifySignature(SOAPMessageContext smc) {
		
		CryptoUtil crypto = new CryptoUtil();
		String signature = null;
		String idEmissor = null;
		
		 try {
			//extrair e remover assinatura e emissorid
			// get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			
			SOAPHeader sh = se.getHeader();
			// se não tiver header manda excepcao
			if (sh == null)
				throw new RuntimeException("Header cannot be null");
			
			// extrair a assinatura e o emissor e depois remover do cabeçalho 
			// da mensagem SOAP tanto a assinatura como o emissor
			// Razão: Estes elementos não pertenciam ao cabecalho
			// quando foi feita a assinatura
			NodeList node = sh.getChildNodes();
			List<Node> nodesToRemove = new ArrayList<Node>();
			
			for(int i = 0; i < node.getLength(); i++){
				
				Node n = node.item(i);
				
				if(n.getNodeName().equals("sgn:Signature")){
					
					signature = n.getTextContent();
					nodesToRemove.add(n);
				}
				
				if(n.getNodeName().equals("idE:IdEmissor")){
					
					idEmissor = n.getTextContent();
					nodesToRemove.add(n);
				}
			}
			
			for(Node n: nodesToRemove){
				n.getParentNode().removeChild(n);
			}
			
			sh.normalize();
			
			//Caso a signature e o emissor não sejam nulos
			//obtem o certificado do emissor e o certificado da CA
			//e verifica se o certificado do emissor é assinado pela CA
			if(idEmissor != null && signature != null){
				
				//obter certificado do idemissor
				CAClient ca = new CAClient(caURL);
				String certificate = ca.getCertificate(idEmissor);
				
				byte[] bytes = certificate.getBytes(StandardCharsets.UTF_8);
				InputStream in = new ByteArrayInputStream(bytes);
				CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
				
				Certificate cert = certFactory.generateCertificate(in);
				
				Certificate trustedCert = CertUtil.getX509CertificateFromResource(certificateCA);
				
				//Testa se o Certificado obtido do emissor é assinado pela CA
				if(!CertUtil.verifySignedCertificate(cert, trustedCert)){
					throw new RuntimeException("The certificate obtained is not signed by CA");
				}
				//Obtem chave publica
				PublicKey pubKey = cert.getPublicKey();
				if(pubKey == null)
					throw new RuntimeException("Public Key in Signature Check is null");
				
				//converte a mensagem soap para bytes
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				msg.writeTo(bos);
				
				//verifica se a assinatura digital é valida
				if(!crypto.verifyDigitalSignature(bos.toByteArray(), pubKey, signature.getBytes())){
					throw new RuntimeException("Digital Signature return ");
				}
			
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Erro no SignatureCheckHandler");
		}
		
	}

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		return true;
	}

}
