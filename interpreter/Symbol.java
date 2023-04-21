package interpreter;

public class Symbol {

    public int name;
    public int type;
    public SymbolType category;

    public Symbol(int name, int type, SymbolType category) {
        this.name = name;
        this.type = type;
        this.category = category;
    }

    @Override
    public String toString() {
        return this.name + ":" + this.type + ":" + this.category;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != Symbol.class) return false;
        Symbol o = (Symbol) other;

        return (this.name == o.name && this.type == o.type && this.category == o.category);
    }

    @Override
    public int hashCode() {
        return name * 31 + type * 51;
    }

}
