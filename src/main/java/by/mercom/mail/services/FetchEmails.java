package by.mercom.mail.services;

import com.sun.mail.imap.IMAPStore;
import org.apache.commons.cli.CommandLine;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Pattern;

public class FetchEmails {

    public FetchEmails(CommandLine cmd) throws MessagingException {
        Folder imapFolder = getImapFolder(cmd);
        imapFolder.open(Folder.READ_WRITE);

        String mask_files = cmd.getOptionValue("f");
        Pattern filesPattern = mask_files == null ? Pattern.compile(".*") : Pattern.compile(mask_files);

        String mask_sender = cmd.getOptionValue("a");
        Pattern fromPattern = mask_sender == null ? Pattern.compile(".*") : Pattern.compile(mask_sender);

        String mask_topic = cmd.getOptionValue("t");
        Pattern subjectPattern = mask_topic == null ? Pattern.compile(".*") : Pattern.compile(mask_topic);

        String catalog = "";
        if (cmd.hasOption("c")) {
            Path path = Paths.get(cmd.getOptionValue("c"));
            if(Files.isDirectory(path)) catalog = path.toString();
        }

        Message[] messages = imapFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        for (Message message : messages) {
            ProcResult result = processMsg(message, fromPattern, subjectPattern, filesPattern, catalog);
            if (result.msgIsProcessed()) {
                message.setFlag(Flags.Flag.SEEN, true);
                if(cmd.hasOption("r")) message.setFlag(Flags.Flag.DELETED, true);
            }
            if(cmd.hasOption("v")) result.printResult();
        }
    }

    private boolean addressSuitable(Address[] addresses, Pattern pattern) {
        for (Address address : addresses) {
            String mail = address.toString();
            mail = mail.contains("<") ? mail.split("<")[1].replace(">", "") : mail;
            if (pattern.matcher(mail).matches()) return true;
        }
        return false;
    }

    private Path getUnicFileName(String catalog, String fileName) {
        if (Files.exists(Paths.get(catalog, fileName))) {
            return getUnicFileName(catalog, "cp" + fileName);
        }else {
            return Paths.get(catalog, fileName);
        }
    }

    private ProcResult processMsg(Message message,
                                  Pattern fromPattern,
                                  Pattern subjectPattern,
                                  Pattern filesPattern,
                                  String catalog) throws MessagingException {

        ProcResult result = new ProcResult();
        if(!addressSuitable(message.getFrom(), fromPattern)){return result;}
        String subject = message.getSubject() == null ? " " : message.getSubject();
        if(!subjectPattern.matcher(subject).matches()){return result;}

        Object body = null;
        try {
            body = message.getContent();
        } catch (IOException ex) {
            result.addError(ex.getMessage());
            return result;
        }
        if(body instanceof Multipart == false){
            return result;
        }

        Multipart multipart =  (Multipart) body;
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            String encFileName = bodyPart.getFileName();
            if(encFileName != null) {
                String fileName = null;
                try {
                    if (encFileName.contains("?==?")) {
                        //encFileName contain two or more words
                        encFileName = encFileName.replace("?==?", "?=&&=?");
                        String words[] = encFileName.split("&&");

                        StringBuilder sb = new StringBuilder();
                        for (String word : words) {
                            sb.append(MimeUtility.decodeText(word));
                        }
                        fileName = sb.toString();
                    } else {
                        fileName = MimeUtility.decodeText(encFileName);
                    }
                } catch (UnsupportedEncodingException e) {
                    result.addFileName(encFileName, false);
                    result.addError(encFileName + ":" + e.getMessage());
                    continue;
                }
                if (filesPattern.matcher(fileName).matches()) {

                    Path pathToFile = getUnicFileName(catalog, fileName);
                    try{
                        Files.copy(bodyPart.getInputStream(), pathToFile);
                        result.addFileName(fileName, true);
                    } catch (Exception ex) {
                        result.addFileName(fileName, false);
                        result.addError(fileName + ":" + ex.getMessage());
                    }
                } else {
                    result.addFileName(fileName, false);
                }
            }
        }
        return result;
    }

    private Folder getImapFolder(CommandLine cmd) throws MessagingException {
        Properties prop = new Properties();
        Session session = Session.getInstance(prop);
        String protocol = cmd.hasOption("sec") ? "imaps" : "imap";
        final IMAPStore store = (IMAPStore) session.getStore(protocol);
        final String server = cmd.getOptionValue("s");
        final String login = cmd.getOptionValue("l");
        final String password = cmd.getOptionValue("p");
        store.connect(server, login, password);
        return store.getFolder("INBOX");
    }
}
