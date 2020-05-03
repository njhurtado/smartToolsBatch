package com.smarttools.converter;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.smarttools.database.DBConnection;
import com.smarttools.database.DinamoDbContext;
import com.smarttools.database.MongoDbConnection;
import com.smarttools.dto.UsersDynamoDTO;
import com.smarttools.dto.VideoMongoDTO;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class VideoConverter  implements Runnable {

    private static final int PROCESADO = 1;
    private static final Logger log = Logger.getLogger(VideoConverter.class);
    private static DBConnection dbConnection;

    private static String rutaBaseProyecto;
    private static String rutaArchivos;
    private static String comando;
    //private static String dominioEmpresa;

    //email config
    private static String from;
    private static String host;
    private static String port;
    private static String mailSubject;
    private static String mailUser;
    private static String mailPassword;
    private static String sqsUser;
    private static String sqsPassword;
    private static String s3User;
    private static String s3Password;
    private static String mongoDataBaseName;
    private static String mongoUsr; 
    private static String mongoPwd; 
    private static String mongoUrl; 
    private static String mongoPort;
    

    private static String remitente;
    
    private static StringBuilder template = new StringBuilder();
    private static boolean isWindows;

    private static final String BUCKET_NAME;
    private static final String QUEUE_NAME;


    private static final String CARPETA_S3_ORIGINAL = "videos/original/";
    private static final String CARPETA_S3_CONVERTED = "videos/convertido/";

    static {
        isWindows = System.getProperty("os.name").startsWith("Windows");

        dbConnection = DBConnection.getInstance();
        rutaArchivos = dbConnection.readProperties("rutaBaseServidor");
        BUCKET_NAME = dbConnection.readProperties("smarttools.s3.bucketName");
        QUEUE_NAME = dbConnection.readProperties("smarttools.sqs.queueName");
        mongoDataBaseName = dbConnection.readProperties("databaseName");
        mongoPort = dbConnection.readProperties("portBD");
        mongoUrl = dbConnection.readProperties("ipBD");
        
        
        //dominioEmpresa = System.getProperty("dominioEmpresa");
        mailUser = System.getenv("SENDGRID_USERNAME");
        mailPassword = System.getenv("SENDGRID_PASSWORD");
        //rutaBaseProyecto = dbConnection.readProperties("rutaBaseProyecto");
        sqsUser = System.getenv("SQS_USERNAME");
        sqsPassword = System.getenv("SQS_PASSWORD");
        s3User = System.getenv("S3_USERNAME");
        s3Password = System.getenv("S3_PASSWORD");
        mongoUsr = System.getenv("MONGO_USERNAME");
        mongoPwd = System.getenv("MONGO_PASSWORD");
        
        File procesados = new File(rutaArchivos);
        //log.info("Test de existencia: " + procesados.getAbsolutePath());
        if (!procesados.exists()) {
            log.info("No se encontr贸 directorio y se va a crear: " + procesados.getAbsolutePath());
            procesados.mkdir();
        }
        
        File convertidos = new File(rutaArchivos.replaceAll("original", "convertido"));
        //log.info("Test de existencia: " + procesados.getAbsolutePath());
        if (!convertidos.exists()) {
            log.info("No se encontr贸 directorio y se va a crear: " + convertidos.getAbsolutePath());
            convertidos.mkdir();
        }

        comando = dbConnection.readProperties("comando");
        /*if (isWindows)
            comando += ".cmd";*/

        from = dbConnection.readProperties("from");
        host = dbConnection.readProperties("host");
        port = dbConnection.readProperties("port");
        mailSubject = dbConnection.readProperties("mailSubject");
        BufferedReader br = new BufferedReader(new InputStreamReader(VideoConverter.class.getResourceAsStream("/template.html")));
        String myLine;
        try {
            while ((myLine = br.readLine()) != null) {
                template.append(myLine);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        remitente = from;  //Para la direcci贸n nomcuenta@gmail.com
        /*Properties props = System.getProperties();
        System.out.println("host -> " + host);
        System.out.println("remitente -> " + remitente);
        props.put("mail.smtp.host", host);  //El servidor SMTP de Google
        props.put("mail.smtp.user", remitente);
        props.put("mail.smtp.clave", mailPassword);    //La clave de la cuenta
        props.put("mail.smtp.auth", "true");    //Usar autenticaci贸n mediante usuario y clave
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
        props.put("mail.smtp.port", port); //El puerto SMTP seguro de Google

        session = Session.getDefaultInstance(props);
        try {
            transport = session.getTransport("smtp");
            transport.connect(host, mailUser, mailPassword);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }*/
    }


    public void run() {
        try {
        	/*VideoMongoDTO to = crearTO();
        	List<VideoMongoDTO> list = new ArrayList<VideoMongoDTO>();
        	list.add(to);
        	log.debug("Se van a almacenar los resultados a la base de datos.");
            storeResultsInDynamoDB(list);*/
            
            //log.debug("Se va a consultar la cola de mensajes");
            String myQueueUrl = "https://sqs.us-west-2.amazonaws.com/930891485524/smarttoolsgrupo8";
            final AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.EU_WEST_2).withCredentials(new AWSStaticCredentialsProvider(
        			new BasicAWSCredentials(sqsUser, sqsPassword))).build();
            
            //final AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.EU_WEST_2).build();
            
            //final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
            //final List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            List<com.amazonaws.services.sqs.model.Message> messages = sqs.receiveMessage(myQueueUrl).getMessages();
            
            if (!messages.isEmpty()) {
                List<VideoMongoDTO> list = loadVideos(messages);
                log.debug("Se van a procesar los archivos recuperados. Cantidad: " + list.size());
                list=processVideos(list);

                log.debug("Se van a almacenar los resultados a la base de datos.");
                storeResultsInMongoDB(list);

                deleteMessagesFromQueue(sqs, messages, myQueueUrl);
                log.debug("Se borraron los mensajes de la cola...");
            } else {
                log.warn("No se procesaron registros");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error", e);
        }
    }

    /*private VideoMongoDTO crearTO() {
    	VideoMongoDTO to = new VideoMongoDTO();
    	to.setConvertedVideoPath("https://smarttoolsgrupo8.s3-us-west-2.amazonaws.com/videos/convertido/1/Video.mp4");
    	to.setCreateDate("2020-04-07");
    	to.setEmail("nj.hurtado@uniandes.edu.co");
    	to.setFirstName("Nelson");
    	to.setIdContest("1");
    	to.setIdUser("1");
    	to.set_id("1");
    	to.setLastName("Hurtado");
    	to.setState("En processo");
    	to.setTittle("Video 1");
    	to.setUrl("http://sitio/video");
    	to.setOriginalVideoPath("https://smarttoolsgrupo8.s3-us-west-2.amazonaws.com/videos/original/1/Video.avi");
    	System.out.println("Video -> " + to);
    	return to;
    }*/
    
    private void deleteMessagesFromQueue(AmazonSQS sqs, List<com.amazonaws.services.sqs.model.Message> messages, String queueUrl) {
        // delete messages from the queue
        for (com.amazonaws.services.sqs.model.Message m : messages) {
            sqs.deleteMessage(queueUrl, m.getReceiptHandle());
        }
    }

    private List<VideoMongoDTO> loadVideos(List<com.amazonaws.services.sqs.model.Message> messages) throws IOException {

        List<VideoMongoDTO> list = new ArrayList<>();
        VideoMongoDTO dto = null;
                ObjectMapper mapper = new ObjectMapper();
        for (com.amazonaws.services.sqs.model.Message message : messages) {
            String jsonInput = message.getBody();
            
            //org.bson.Document doc = org.bson.Document.parse(jsonInput);
            
            //String otro = com.mongodb.util.JSON.serialize(doc);
            
            //log.info("Mensaje " + jsonInput);
            
            //log.info("Objeto JSON " + otro);
            
            //log.info("Se recibe el mensaje " + jsonInput);
            //Se elimina el ObjectId() si lo trae
            
            if(jsonInput.indexOf("'") > 0) {
            	jsonInput = jsonInput.replaceAll("'", "\"");
            	jsonInput = jsonInput.replaceAll("None", "\"None\"");
            }
            
            
            int lim1 = jsonInput.indexOf("ObjectId(");
            int lim2 = lim1+ 35;//jsonInput.lastIndexOf(")");
            
            int lim3 = jsonInput.indexOf("date");
            int lim4 = jsonInput.indexOf("status");
            
            String s = jsonInput.substring(lim1+ 9, lim2);
            //log.info("parte " + s);
            //String reemplazar = jsonInput.substring(lim1, lim2+1);
            String json = jsonInput.substring(0, lim1);
            json += s + jsonInput.substring(lim2+1);
            
            if(lim3 > 0) {
	            //Se quita el elemento 'date': datetime.datetime( si viene diligenciado
	            String parte2 = json.substring(0, (lim3-12));
	            parte2 += jsonInput.substring(lim4-1);
	            //log.info("reemplazo2  " + parte2);
	            json = parte2;
            }
            
            TypeReference<HashMap<String, Map>> typeRef
                    = new TypeReference<HashMap<String, Map>>() {
            };
            VideoMongoDTO dtoRfe = mapper.readValue(json, VideoMongoDTO.class);
            if(dtoRfe != null) {
            	list.add(dtoRfe);
            }
            
        }
        return list;
    }

    private List<VideoMongoDTO> processVideos(List<VideoMongoDTO> set) {

        List<VideoMongoDTO> result=new ArrayList<>();

        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern("yyyy-MM-dd_HH-mm-ss");
        //caption, nombre de archivo in y nombre archivo out
        String[] args = new String[3];

        if (set == null) {
            log.error("no se van a procesar los videos");
            return result;
        }

        for (VideoMongoDTO design : set) {

        	long tiempoInicial = System.currentTimeMillis();
            VideoMongoDTO to=null;
            //videos/original/1/Video.avi
            int indexSlash = design.getOriginal_video_path().lastIndexOf('/') + 1;
            int indexDot = design.getOriginal_video_path().lastIndexOf('.');
            int indexImagenes = design.getOriginal_video_path().lastIndexOf(CARPETA_S3_ORIGINAL) + 16;

            String videoname = design.getOriginal_video_path().substring(indexSlash, indexDot);
            String videoExtension = design.getOriginal_video_path().substring(indexDot);
            //String nombreArchivoOut = rutaArchivos + imagename + "-procesado.png";
            //String rutaArchivoIn = rutaArchivos + design.getVideoPath().substring(indexSlash);
            String id = design.getOriginal_video_path().substring(indexImagenes, indexSlash - 1);
            String rutaArchivoOrigLocal=rutaArchivos + videoname + videoExtension;
            //String nombreArchivoOut=rutaArchivos+design.getFirstName()+"-"+df.format(new Date())+".mp4";
            String nombreArchivoOut=rutaArchivos+videoname+".mp4";
            nombreArchivoOut = nombreArchivoOut.replaceAll("original", "convertido");
            try {
            	//log.debug("id archivo : " + id);
                //log.debug("indexImagenes :" + indexImagenes);
                log.debug("Se va a descargar el video: " + design.getOriginal_video_path());
                log.debug("A la ruta local :" + rutaArchivoOrigLocal);
                
                getFileFromS3(design.getOriginal_video_path(), rutaArchivoOrigLocal);

                log.debug("Se descarga el video a :" + rutaArchivoOrigLocal);

                //hay que consultar la base de datos
                //log.debug("Se va a consutlar el video: "+design.get_id()+":"+design.getCreateDate());
                /*Optional<VideoMongoDTO> designDTODB = findDynamoById(design.getIdVideo(),design.getCreateDate());

                if (!designDTODB.isPresent()) {
                    throw new RuntimeException("No se encontro el video眔 " + design.getIdVideo() + " en la base de datos");
                }
                to=designDTODB.get();*/
                
                args[0]=videoname + videoExtension;
                //args[1]=rutaBaseProyecto+rutaArchivoIn;
                //args[2]=rutaBaseProyecto+nombreArchivoOut;
                args[1]=rutaArchivoOrigLocal;
                args[2]= nombreArchivoOut;
                log.debug(" Ruta origen ->" + args[1]);
                log.debug(" Ruta destino ->"  + nombreArchivoOut);
                
                String cmd = comando+" "+args[1]+" -vcodec h264 "+args[2] + "\n";
                log.debug(" comando ->"  + cmd);
                
                /*File archivo1 = new File(args[1]);
                File archivo2 = new File(args[2]);
                
                if(archivo1.exists()) {
                	log.debug(" El archivo ->"  + archivo1.getAbsolutePath() + "existe");
                } else {
                	log.debug(" El archivo ->"  + archivo1.getAbsolutePath() + " NO existe");
                }
                
                if(archivo2.exists()) {
                	log.debug(" El archivo ->"  + archivo2.getAbsolutePath() + "existe");
                } else {
                	log.debug(" El archivo ->"  + archivo2.getAbsolutePath() + " NO existe");
                }*/
                
                Process process=Runtime.getRuntime().exec(cmd);


                //Despu茅s se lee la salida est谩ndar y se registran los resultados en el log

                StringBuilder output = new StringBuilder();
                StringBuilder error=new StringBuilder();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));//getInputStream
                String line;
                
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }
                reader=new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    error.append(line + "\n");
                }
                int exitVal = process.waitFor();
                process.destroy();
                reader.close();
                if (exitVal == 0) {
                    log.debug("Success!");
                    //nombreArchivoOut = rutaBaseProyecto + nombreArchivoOut;
                    log.debug("Se sube el archivo a: " + nombreArchivoOut);
                    File archivo = new File(nombreArchivoOut);
                    nombreArchivoOut = sendToS3(id, archivo);
                    design.setConverted_video_path(nombreArchivoOut);
                    //log.debug("Se sube el archivo a s3!");
                    sendEmail(design.getEmail(), design.getConverted_video_path());
                    //log.debug("Se envia email!");
                    result.add(design);
                    archivo.delete();
                } else {
                    log.error(comando + cmd);
                    log.error("Error Despues de convertir !");
                    design.setResult(error.toString());
                    log.error(error);
                }

            } catch (IOException e) {
                log.error(comando+" "+args[1]+" -vcodec h264 "+args[2] );
                design.setResult(e.getMessage());
                log.error("Error:", e);
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error: ", e);
            } finally {
            	design.setConverted_video_path(nombreArchivoOut);
                log.debug("Termina el proceso del video en " + (System.currentTimeMillis() - tiempoInicial) + " ms.");
            }

        }
        return result;
    }

    //---------- Inicio Operaciones - DynamoDB --------------------------

    private Optional<VideoMongoDTO> findDynamoById(String id,Long date) {
        return Optional.ofNullable(DinamoDbContext.getMapper().load(VideoMongoDTO.class,id,date));
    }
    private Optional<UsersDynamoDTO> findUserDynamoById(String id) {
        return Optional.ofNullable(DinamoDbContext.getMapper().load(UsersDynamoDTO.class,id));
    }

    private void storeResultsInDynamoDB(List<VideoMongoDTO> set) {
        for (VideoMongoDTO design : set) {
            if (design.getResult() != null && !design.getResult().equals("")) {
                log.warn("no se proces贸 un registro: " + design.toString());
                continue;
            }
            design.setStatus("Procesado");
            DynamoDBMapperConfig dynamoDBMapperConfig = new DynamoDBMapperConfig.Builder()
                    .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                    .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE)
                    .build();
            DinamoDbContext.getMapper().save(design, dynamoDBMapperConfig);
        }
    }

    private void storeResultsInMongoDB(List<VideoMongoDTO> set) {
    	
    	MongoDatabase database = MongoDbConnection.getInstance(mongoDataBaseName, mongoUsr, mongoPwd, mongoUrl, mongoPort); 
    	//log.info("se establece la conexion" );
        for (VideoMongoDTO design : set) {
            if (design.getResult() != null && !design.getResult().equals("")) {
                log.warn("error en el registro: " + design.toString());
                continue;
            }
            design.setStatus("Procesado");
            
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(design.get_id()));

            BasicDBObject newDocument = convertirADocument(design);

            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument); 
            
            MongoCollection<Document> col = database.getCollection("contestmain_video");
            /*if(col != null) {
            	FindIterable<Document> iterable = col.find();
                iterable.forEach(new Block<Document>() {
                    @Override
                    public void apply(final Document document) {
                        System.out.println("se encuentre el registro: " + document);
                    }
                });  
            }*/
            
            FindIterable<Document> doc = col.find(query);
            doc.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    System.out.println("se encuentra el registro a actualizar: " + document);
                }
            }); 
            
            col.updateOne(query, updateObject);
            
            FindIterable<Document> doc2 = col.find(query);
            doc2.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    System.out.println("se consulta el registro actualizado: " + document);
                }
            }); 
            log.info("se procesa el registro: " + design.get_id());
        }
    }
    
    private BasicDBObject convertirADocument(VideoMongoDTO videoTo) {
    	BasicDBObject document = new BasicDBObject();
        //document.put("_id", videoTo.get_id());
        //document.put("first_name", videoTo.getFirst_name());
        //document.put("last_Name", videoTo.getLastName());
        document.put("status", videoTo.getStatus());
        //document.put("originalVideoPath", videoTo.getOriginalVideoPath());
        document.put("converted_video_path", videoTo.getConverted_video_path());
        //document.put("tittle", videoTo.getTittle());
        //document.put("date", videoTo.getCreateDate());
        //document.put("email", videoTo.getEmail());
        //document.put("url", videoTo.getUrl());
        //document.put("idUser", videoTo.getIdUser());
        //document.put("idContest", videoTo.getIdContest());
        
        return document;
    }
    
    //---------- Fin Operaciones - DynamoDB ----------------------------

    private String sendToS3(String id, File img) {

        String idObjectImg = CARPETA_S3_CONVERTED + id + "/" + img.getName();
        //String bucket = "s3-us-west-2.amazonaws.com/smarttoolsgrupo8";
        log.debug("se va a guardar el objeto " + BUCKET_NAME + ":" + idObjectImg);
        //log.debug("consultando origen " + img.getAbsolutePath());

        //final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_2).build();
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(
    			new BasicAWSCredentials(s3User, s3Password))).build();

        //s3.putObject(BUCKET_NAME, idObjectImg, img);
        s3.putObject(new PutObjectRequest(BUCKET_NAME, idObjectImg, img)
        	      .withCannedAcl(CannedAccessControlList.PublicRead));

        //log.debug("fin de la publicaci贸n");
        return s3.getUrl(BUCKET_NAME, idObjectImg).toExternalForm();

    }

    private void getFileFromS3(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        try (
                InputStream is = url.openStream();
                OutputStream os = new FileOutputStream(destinationFile);
        ) {
            byte[] b = new byte[2048];
            int length;

            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
        }
    }

   

    private final static Object lock = new Object();

    private void sendEmail(String emailAddress, String urlProyecto) {

        Runnable r = () -> {
            //log.debug("Enviando correo a..." + emailAddress);
            //log.debug("Desde.." + remitente);
            try {
            	Email from = new Email(remitente);
                String subject = mailSubject;
                Email to = new Email(emailAddress);
                Content content = new Content("text/html", template.toString().replace(":url", urlProyecto));
                Mail mail = new Mail(from, subject, to, content);

                SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
                Request request = new Request();
                
                synchronized (lock) {
                	request.setMethod(Method.POST);
                    request.setEndpoint("mail/send"); 
                    request.setBody(mail.build());
                    Response response = sg.api(request);
                    System.out.println(response.getStatusCode());
                    System.out.println(response.getBody());
                    System.out.println(response.getHeaders());

                }
                
                
            } catch (IllegalStateException e) {
            	e.printStackTrace();
                log.error("Error: ", e);


            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        };

        Thread th = new Thread(r);
        th.start();


    }
}
