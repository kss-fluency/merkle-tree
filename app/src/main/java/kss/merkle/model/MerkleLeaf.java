package kss.merkle.model;

import com.google.common.io.BaseEncoding;
import kss.merkle.crypto.Hasher;
import lombok.Getter;

@Getter
public class MerkleLeaf extends MerkleNode {
    private final String data;

    public MerkleLeaf(String data, Integer depth, Hasher hasher) {
        super(hasher);
        this.data = data;
        this.depth = depth;
        this.hash = hasher.hash(data.getBytes());
    }

    @Override
    public String toString() {
        return "--".repeat(Math.max(0, depth)) +
                String.format("MerkleLeaf{ hash=[%s] data=[%s] }\n", BaseEncoding.base16().lowerCase().encode(hash), data);
    }
}
