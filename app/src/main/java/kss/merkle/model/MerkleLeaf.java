package kss.merkle.model;

import com.google.common.io.BaseEncoding;
import kss.merkle.crypto.Sha256Hasher;
import lombok.Getter;

@Getter
public class MerkleLeaf extends MerkleNode {
    private final String data;

    public MerkleLeaf(String data) {
        this.data = data;
        this.hash = Sha256Hasher.hash(data.getBytes());
    }

    @Override
    public String toString() {
        return String.format("MerkleLeaf{ hash=%s data=%s }\n", BaseEncoding.base16().lowerCase().encode(hash), data);
    }
}
