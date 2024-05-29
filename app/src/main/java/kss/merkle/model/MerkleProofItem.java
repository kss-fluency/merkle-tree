package kss.merkle.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface MerkleProofItem {

    @AllArgsConstructor
    @Getter
    class Left implements MerkleProofItem {
        private final byte[] hash;
    }

    @AllArgsConstructor
    @Getter
    class Right implements MerkleProofItem {
        private final byte[] hash;
    }
}
