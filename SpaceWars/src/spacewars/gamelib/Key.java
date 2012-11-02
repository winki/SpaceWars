package spacewars.gamelib;

public enum Key
{
    ENTER('\n'),
    BACK_SPACE('\b'),
    TAB('\t'),
    CANCEL(0x03),
    CLEAR(0x0C),
    SHIFT(0x10),
    CONTROL(0x11),
    ALT(0x12),
    PAUSE(0x13),
    CAPS_LOCK(0x14),
    ESCAPE(0x1B),
    SPACE(0x20),
    PAGE_UP(0x21),
    PAGE_DOWN(0x22),
    END(0x23),
    HOME(0x24),
    LEFT(0x25),
    UP(0x26),
    RIGHT(0x27),
    DOWN(0x28),
    COMMA(0x2C),
    MINUS(0x2D),
    PERIOD(0x2E),
    SLASH(0x2F),
    D0(0x30),
    D1(0x31),
    D2(0x32),
    D3(0x33),
    D4(0x34),
    D5(0x35),
    D6(0x36),
    D7(0x37),
    D8(0x38),
    D9(0x39),
    SEMICOLON(0x3B),
    EQUALS(0x3D),
    A(0x41),
    B(0x42),
    C(0x43),
    D(0x44),
    E(0x45),
    F(0x46),
    G(0x47),
    H(0x48),
    I(0x49),
    J(0x4A),
    K(0x4B),
    L(0x4C),
    M(0x4D),
    N(0x4E),
    O(0x4F),
    P(0x50),
    Q(0x51),
    R(0x52),
    S(0x53),
    T(0x54),
    U(0x55),
    V(0x56),
    W(0x57),
    X(0x58),
    Y(0x59),
    Z(0x5A),
    OPEN_BRACKET(0x5B),
    BACK_SLASH(0x5C),
    CLOSE_BRACKET(0x5D),
    NUMPAD0(0x60),
    NUMPAD1(0x61),
    NUMPAD2(0x62),
    NUMPAD3(0x63),
    NUMPAD4(0x64),
    NUMPAD5(0x65),
    NUMPAD6(0x66),
    NUMPAD7(0x67),
    NUMPAD8(0x68),
    NUMPAD9(0x69),
    MULTIPLY(0x6A),
    ADD(0x6B),
    SEPARATER(0x6C),
    SUBTRACT(0x6D),
    DECIMAL(0x6E),
    DIVIDE(0x6F),
    DELETE(0x7F),
    NUM_LOCK(0x90),
    SCROLL_LOCK(0x91),
    F1(0x70),
    F2(0x71),
    F3(0x72),
    F4(0x73),
    F5(0x74),
    F6(0x75),
    F7(0x76),
    F8(0x77),
    F9(0x78),
    F10(0x79),
    F11(0x7A),
    F12(0x7B),
    F13(0xF000),
    F14(0xF001),
    F15(0xF002),
    F16(0xF003),
    F17(0xF004),
    F18(0xF005),
    F19(0xF006),
    F20(0xF007),
    F21(0xF008),
    F22(0xF009),
    F23(0xF00A),
    F24(0xF00B),
    PRINTSCREEN(0x9A),
    INSERT(0x9B),
    HELP(0x9C),
    META(0x9D),
    BACK_QUOTE(0xC0),
    QUOTE(0xDE),
    KP_UP(0xE0),
    KP_DOWN(0xE1),
    KP_LEFT(0xE2),
    KP_RIGHT(0xE3),
    DEAD_GRAVE(0x80),
    DEAD_ACUTE(0x81),
    DEAD_CIRCUMFLEX(0x82),
    DEAD_TILDE(0x83),
    DEAD_MACRON(0x84),
    DEAD_BREVE(0x85),
    DEAD_ABOVEDOT(0x86),
    DEAD_DIAERESIS(0x87),
    DEAD_ABOVERING(0x88),
    DEAD_DOUBLEACUTE(0x89),
    DEAD_CARON(0x8a),
    DEAD_CEDILLA(0x8b),
    DEAD_OGONEK(0x8c),
    DEAD_IOTA(0x8d),
    DEAD_VOICED_SOUND(0x8e),
    DEAD_SEMIVOICED_SOUND(0x8f),
    AMPERSAND(0x96),
    ASTERISK(0x97),
    QUOTEDBL(0x98),
    LESS(0x99),
    GREATER(0xa0),
    BRACELEFT(0xa1),
    BRACERIGHT(0xa2),
    AT(0x0200),
    COLON(0x0201),
    CIRCUMFLEX(0x0202),
    DOLLAR(0x0203),
    EURO_SIGN(0x0204),
    EXCLAMATION_MARK(0x0205),
    INVERTED_EXCLAMATION_MARK(0x0206),
    LEFT_PARENTHESIS(0x0207),
    NUMBER_SIGN(0x0208),
    PLUS(0x0209),
    RIGHT_PARENTHESIS(0x020A),
    UNDERSCORE(0x020B),
    WINDOWS(0x020C),
    CONTEXT_MENU(0x020D),
    FINAL(0x0018),
    CONVERT(0x001C),
    NONCONVERT(0x001D),
    ACCEPT(0x001E),
    MODECHANGE(0x001F),
    KANA(0x0015),
    KANJI(0x0019),
    ALPHANUMERIC(0x00F0),
    KATAKANA(0x00F1),
    HIRAGANA(0x00F2),
    FULL_WIDTH(0x00F3),
    HALF_WIDTH(0x00F4),
    ROMAN_CHARACTERS(0x00F5),
    ALL_CANDIDATES(0x0100),
    PREVIOUS_CANDIDATE(0x0101),
    CODE_INPUT(0x0102),
    JAPANESE_KATAKANA(0x0103),
    JAPANESE_HIRAGANA(0x0104),
    JAPANESE_ROMAN(0x0105),
    KANA_LOCK(0x0106),
    INPUT_METHOD_ON_OFF(0x0107),
    CUT(0xFFD1),
    COPY(0xFFCD),
    PASTE(0xFFCF),
    UNDO(0xFFCB),
    AGAIN(0xFFC9),
    FIND(0xFFD0),
    PROPS(0xFFCA),
    STOP(0xFFC8),
    COMPOSE(0xFF20),
    ALT_GRAPH(0xFF7E),
    BEGIN(0xFF58),
    UNDEFINED(0x0);
    
    private int keyCode;
    
    Key(int keyCode)
    {
        this.keyCode = keyCode;
    }
    
    public int keyCode()
    {
        return keyCode;
    }
}
