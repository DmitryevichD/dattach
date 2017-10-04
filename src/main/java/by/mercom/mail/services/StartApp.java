package by.mercom.mail.services;

import org.apache.commons.cli.*;

import javax.mail.MessagingException;

public class StartApp {
    private static Options options = new Options();

    static {
        Option mServer = Option.builder("s")
                .longOpt("server")
                .hasArg()
                .argName("imap server")
                .desc("Imap mail server where attachments will be downloaded (example: imap.name.org)")
                .required()
                .type(String.class)
                .build();

        Option mLogin = Option.builder("l")
                .longOpt("login")
                .hasArg()
                .argName("mail login")
                .desc("Login with which you access your e-mail")
                .required()
                .type(String.class)
                .build();

        Option mPwd = Option.builder("p")
                .longOpt("password")
                .hasArg()
                .argName("password")
                .desc("Password for accessing the mailbox")
                .required()
                .type(String.class)
                .build();

        Option mSec = Option.builder()
                .longOpt("sec")
                .hasArg(false)
                .desc("Use security connection to mail server on imap protocol 993")
                .build();

        Option removeMails = Option.builder("r")
                .longOpt("remove")
                .hasArg(false)
                .desc("Delete emails that have been processed")
                .build();

        Option directory = Option.builder("c")
                .longOpt("catalog")
                .hasArg()
                .argName("path to catalog")
                .desc("Path to the directory into which the files will be saved")
                .build();

        Option help = Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .desc("print this message")
                .build();

        Option maskSender = Option.builder("a")
                .longOpt("mask-address")
                .hasArg()
                .desc("Java regular expression pattern for filtering sender email address. This mails will be processed")
                .argName("RegExpr")
                .type(String.class)
                .build();
        Option maskFiles = Option.builder("f")
                .longOpt("mask-files")
                .hasArg()
                .desc("Java regular expression pattern for filtering attached files. This files will be downloaded")
                .argName("RegExpr")
                .type(String.class)
                .build();
        Option verbose = Option.builder("v")
                .longOpt("verbose")
                .desc("Verbose info about processing")
                .build();

        Option maskTopic = Option.builder("t")
                .longOpt("mask-topic")
                .hasArg()
                .desc("Java regular expression pattern for filtering email topic. This mails will be processed")
                .argName("RegExpr")
                .type(String.class)
                .build();

        options.addOption(mServer);
        options.addOption(mLogin);
        options.addOption(mSec);
        options.addOption(mPwd);
        options.addOption(removeMails);
        options.addOption(directory);
        options.addOption(maskSender);
        options.addOption(maskFiles);
        options.addOption(maskTopic);
        options.addOption(help);
    }

    private static void printHelp(){
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("dattach [OPTION]", options);
    }

    public static void main(String[] args){
        if (args.length == 0){ printHelp(); return;}

        CommandLineParser parser = new DefaultParser();
        try {
            final CommandLine cmd = parser.parse(options, args);
            new FetchEmails(cmd);
            System.exit(0);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            printHelp();
            System.exit(-1);
        } catch (MessagingException ex) {
            System.out.println(ex.getMessage());
            printHelp();
            System.exit(-2);
        }

    }


}
