package kss.merkle.model;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import kss.merkle.crypto.Hasher;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MerkleNode {
    protected byte[] hash;
    @Setter
    private MerkleNode left;
    @Setter
    private MerkleNode right;

    protected Integer depth;
    protected final Hasher hasher;

    public MerkleNode(Hasher hasher) {
        this.hasher = hasher;
    }

    public MerkleNode(MerkleNode left, MerkleNode right, Integer depth, Hasher hasher) {
        this(hasher);
        this.depth = depth;
        this.left = left;
        this.right = right;
        updateHash();
    }

    public void updateHash() {
        this.hash = hasher.hash(Bytes.concat(left.hash, right.hash));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--".repeat(Math.max(0, depth)));
        sb.append(String.format("MerkleNode{ hash=[%s] }\n", BaseEncoding.base16().lowerCase().encode(hash)));
        if (left != null) sb.append(left);
        if (right != null) sb.append(right);

        return sb.toString();
    }
}
