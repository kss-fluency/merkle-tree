package kss.merkle.model;

import com.google.common.io.BaseEncoding;
import kss.merkle.crypto.Hasher;
import kss.merkle.exception.MerkleException;
import lombok.Getter;

@Getter
public class MerkleLeaf extends MerkleNode {
    private String data;

    public MerkleLeaf(String data, Integer depth, Hasher hasher) throws MerkleException {
        super(hasher);
        this.depth = depth;
        updateData(data);
    }

    public void updateData(String data) throws MerkleException {
        if (data == null) {
            throw new MerkleException("Leaf data must not be null");
        }
        this.data = data;
        this.hash = hasher.hash(data.getBytes());
    }

    @Override
    public void updateHash() throws MerkleException {
        throw new MerkleException("There is no need to update hash unless you update data. use MerkleLeaf.updateData if you would like to do that.");
    }

    @Override
    public String toString() {
        return "--".repeat(Math.max(0, depth)) +
                String.format("MerkleLeaf{ hash=[%s] data=[%s] }\n", BaseEncoding.base16().lowerCase().encode(hash), data);
    }
}
