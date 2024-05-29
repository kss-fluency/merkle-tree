package kss.merkle.model;

import com.google.common.io.BaseEncoding;
import kss.merkle.crypto.Sha256Hasher;
import lombok.Getter;

@Getter
public class MerkleLeaf extends MerkleNode {
    private final String data;
    private final Integer depth;

    public MerkleLeaf(String data, Integer depth) {
        this.data = data;
        this.depth = depth;
        this.hash = Sha256Hasher.hash(data.getBytes());
    }

    @Override
    public String toString() {
        return "--".repeat(Math.max(0, depth)) +
                String.format("MerkleLeaf{ hash=[%s] data=[%s] }\n", BaseEncoding.base16().lowerCase().encode(hash), data);
    }
}
