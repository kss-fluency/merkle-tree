package kss.merkle.model;

import com.google.common.io.BaseEncoding;
import kss.merkle.crypto.Sha256Hasher;

public class MerkleLeaf extends MerkleNode {

    public MerkleLeaf(String data) {
        this.hash = Sha256Hasher.hash(data.getBytes());
    }

    @Override
    public String toString() {
        return String.format("MerkleLeaf{ hash=%s }\n", BaseEncoding.base16().lowerCase().encode(hash));
    }
}
