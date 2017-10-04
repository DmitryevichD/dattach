package by.mercom.mail.services;

import org.junit.Test;
import static org.junit.Assert.*;

public class ProsResultTest {
    @Test
    public void ProcResultTest() throws Exception{
        ProcResult proc = new ProcResult();
        assertFalse(proc.msgIsProcessed());

        proc.addFileName("some file", true);
        proc.addError("some error");
        assertFalse(proc.msgIsProcessed());

        proc = new ProcResult();
        proc.addFileName("some file", true);
        proc.addFileName("some file1", false);
        proc.addFileName("some file2", true);
        assertFalse(proc.msgIsProcessed());

        proc = new ProcResult();
        proc.addFileName("some file", true);
        proc.addFileName("some file1", true);
        proc.addFileName("some file2", true);
        proc.addFileName("some file3", true);
        assertTrue(proc.msgIsProcessed());
    }
}
