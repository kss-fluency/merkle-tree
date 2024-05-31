package kss.merkle.model;

import com.google.common.io.BaseEncoding;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface MerkleProofItem {

    byte[] getHash();

    @AllArgsConstructor
    @Getter
    class Left implements MerkleProofItem {
        private final byte[] hash;

        @Override
        public String toString() {
            return String.format("MerkleProofItem.Left{ hash=[%s] }\n", BaseEncoding.base16().lowerCase().encode(hash));
        }
    }

    @AllArgsConstructor
    @Getter
    class Right implements MerkleProofItem {
        private final byte[] hash;

        @Override
        public String toString() {
            return String.format("MerkleProofItem.Right{ hash=[%s] }\n", BaseEncoding.base16().lowerCase().encode(hash));
        }
    }
}
