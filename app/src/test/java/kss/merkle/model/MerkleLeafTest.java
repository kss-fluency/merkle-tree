package kss.merkle.model;


import kss.merkle.crypto.Hasher;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class MerkleLeafTest {
    private static final Hasher mockHasher = Mockito.mock(Hasher.class);
    private static final byte[] SOME_HASH = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};

    private final Integer SOME_DEPTH = 2;
    private final String SOME_DATA = "0xdeadbeef";

    @BeforeClass
    public static void mockHasher() {
        Mockito.when(mockHasher.hash(any())).thenReturn(SOME_HASH);
    }

    @Test
    public void shouldCreateLeafAndHashData() {
        MerkleLeaf node = new MerkleLeaf(SOME_DATA, SOME_DEPTH, mockHasher);

        assertThat(node.getDepth()).isEqualTo(SOME_DEPTH);
        assertThat(node.getHash()).isEqualTo(SOME_HASH);
        assertThat(node.getData()).isEqualTo(SOME_DATA);
        assertThat(node.getLeft()).isNull();
        assertThat(node.getRight()).isNull();
    }
}