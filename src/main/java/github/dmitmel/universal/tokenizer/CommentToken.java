package github.dmitmel.universal.tokenizer;

public class CommentToken extends Token {
    private String value;
    public CommentToken(String value, int indexInCode) {
        super(indexInCode);
        this.value = value;
    }
    public CommentToken(String value) {
        this(value, -1);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return Type.COMMENT;
    }
}
