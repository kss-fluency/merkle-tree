package kss.merkle.model;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import kss.merkle.crypto.Sha256Hasher;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MerkleNode {
    protected byte[] hash;
    @Setter
    private MerkleNode left;
    @Setter
    private MerkleNode right;
    private Integer depth;

    public MerkleNode() {
    }

    public MerkleNode(MerkleNode left, MerkleNode right, Integer depth) {
        this.depth = depth;
        this.hash = Sha256Hasher.hash(Bytes.concat(left.hash, right.hash));
        this.left = left;
        this.right = right;
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
