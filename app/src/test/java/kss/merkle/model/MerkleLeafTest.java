package kss.merkle.model;


import kss.merkle.crypto.Hasher;
import kss.merkle.exception.MerkleException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

public class MerkleLeafTest {
    private static final Hasher mockHasher = Mockito.mock(Hasher.class);
    private static final byte[] SOME_HASH = new byte[]{1};
    private static final byte[] SOME_OTHER_HASH = new byte[]{2};

    private final Integer SOME_DEPTH = 2;
    private final String SOME_DATA = "0xdeadbeef";
    private final String SOME_OTHER_DATA = "0xfeedbabe";

    @BeforeClass
    public static void mockHasher() {
        Mockito.when(mockHasher.hash(any())).thenReturn(SOME_HASH);
    }

    @After
    public void cleanup() {
        Mockito.when(mockHasher.hash(any())).thenReturn(SOME_HASH);
    }

    @Test
    public void shouldCreateLeafAndHashData() throws MerkleException {
        MerkleLeaf node = new MerkleLeaf(SOME_DATA, SOME_DEPTH, mockHasher);

        assertThat(node.getDepth()).isEqualTo(SOME_DEPTH);
        assertThat(node.getHash()).isEqualTo(SOME_HASH);
        assertThat(node.getData()).isEqualTo(SOME_DATA);
        assertThat(node.getLeft()).isNull();
        assertThat(node.getRight()).isNull();
    }

    @Test
    public void shouldNotCreateLeafWithNullData() {
        assertThatThrownBy(() -> new MerkleLeaf(null, SOME_DEPTH, mockHasher))
                .isInstanceOf(MerkleException.class)
                .hasMessage("Leaf data must not be null")
        ;
    }

    @Test
    public void shouldUpdateDataAndHash() throws MerkleException {
        MerkleLeaf node = new MerkleLeaf(SOME_DATA, SOME_DEPTH, mockHasher);

        assertThat(node.getData()).isEqualTo(SOME_DATA);
        assertThat(node.getHash()).isEqualTo(SOME_HASH);

        Mockito.when(mockHasher.hash(any())).thenReturn(SOME_OTHER_HASH);
        node.updateData(SOME_OTHER_DATA);
        assertThat(node.getData()).isEqualTo(SOME_OTHER_DATA);
        assertThat(node.getHash()).isEqualTo(SOME_OTHER_HASH);
    }
}