package org.example.is_lab1.transactions;

import javax.transaction.xa.Xid;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;


public class SimpleXid implements Xid {
    private static final int FORMAT_ID = 0x49534C; 
    private final byte[] globalTransactionId;
    private final byte[] branchQualifier;

    private SimpleXid(byte[] globalTransactionId, byte[] branchQualifier) {
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
    }

    public static SimpleXid random() {
        return new SimpleXid(uuidToBytes(UUID.randomUUID()), uuidToBytes(UUID.randomUUID()));
    }

    public static SimpleXid fromString(String value) {
        String[] parts = value.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Malformed Xid: " + value);
        }
        return new SimpleXid(
                Base64.getDecoder().decode(parts[0]),
                Base64.getDecoder().decode(parts[1])
        );
    }

    public String asString() {
        return Base64.getEncoder().encodeToString(globalTransactionId) + ":" +
                Base64.getEncoder().encodeToString(branchQualifier);
    }

    @Override
    public int getFormatId() {
        return FORMAT_ID;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return globalTransactionId;
    }

    @Override
    public byte[] getBranchQualifier() {
        return branchQualifier;
    }

    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }
}
