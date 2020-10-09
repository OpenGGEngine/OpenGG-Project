package com.opengg.core.render.shader.ggsl;

import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.util.LambdaContainer;

import java.util.*;
import java.util.stream.Collectors;

import static com.opengg.core.render.shader.ggsl.TokenType.*;

public class Parser {

    private final Lexer lexer;
    private int current;

    public Parser(Lexer lexer){
        this.lexer = lexer;
    }

    public AbstractSyntaxTree parse(){
        AbstractSyntaxTree tree = new AbstractSyntaxTree();
        while(!accept(EOF).isPresent()){
            if(findNext(SEMICOLON) < findNext(OPEN_BRACE)){
                tree.nodes.add(parseDeclaration());
            }else if(findNext(STRUCT) < findNext(OPEN_BRACE)){
                tree.nodes.add(parseStruct());
            }else if(findNext(UNIFORM, IN, OUT, INOUT) < findNext(OPEN_BRACE)){
                tree.nodes.add(parseInterface());
            }else{
                tree.nodes.add(parseFunction());
            }
        }

        return tree;
    }

    public Function parseFunction(){
        Function function = new Function();
        var modifierStack = new ArrayDeque<String>();
        while(!(next(OPEN_PARENTHESIS))){
            modifierStack.push(consume(IDENTIFIER).contents());
        }

        function.name = new Identifier(modifierStack.pop());
        function.type = new Identifier(modifierStack.pop());
        function.modifiers.modifiers = modifierStack.stream().map(Modifier::new).collect(Collectors.toList());

        consume(OPEN_PARENTHESIS);

        while(!(next(CLOSE_PARENTHESIS))){
            Declaration declaration = new Declaration();

            modifierStack = new ArrayDeque<>();
            while(!(next(COMMA) || next(CLOSE_PARENTHESIS))){
                modifierStack.push(consume(IDENTIFIER, UNIFORM).contents());
            }

            declaration.name = new Identifier(modifierStack.pop());
            declaration.type = new Identifier(modifierStack.pop());
            declaration.modifiers.modifiers = modifierStack.stream().map(Modifier::new).collect(Collectors.toList());

            accept(COMMA);

            function.args.args.add(declaration);
        }

        consume(CLOSE_PARENTHESIS);
        consume(OPEN_BRACE);

        function.body = parseBody();

        return function;
    }

    public Body parseBody(){
        Body body = new Body();
        while(!next(CLOSE_BRACE)){
            body.expressions.add(parseStatement());
        }

        consume(CLOSE_BRACE);
        return body;
    }

    public Node parseStatement(){
        var exp = new LambdaContainer<Node>();
        accept(IF).ifPresent(i -> {
            If iff = new If();
            exp.value = iff;
            consume(OPEN_PARENTHESIS);
            iff.conditional = parseExpression();
            accept(OPEN_BRACE).ifPresentOrElse(o -> iff.then = parseBody(), () -> {
                Body body2 = new Body();
                body2.expressions.add(parseStatement());
                iff.then = body2;
            });

            accept(ELSE).ifPresent(e -> accept(OPEN_BRACE).ifPresentOrElse(o -> iff.els = parseBody(), () -> {
                Body body2 = new Body();
                body2.expressions.add(parseStatement());
                iff.els = body2;
            }));
        });
        if(exp.value != null) return exp.value;
        accept(FOR).ifPresent(i -> {
            For forr = new For();
            exp.value = forr;
            consume(OPEN_PARENTHESIS);
            forr.assignment = parseSingleStatement();
            forr.conditional = parseSingleStatement();
            forr.modifier = parseSingleStatement();

            accept(OPEN_BRACE).ifPresentOrElse(ii ->
                            forr.contents = parseBody(),
                    () -> {
                        Body body1 = new Body();
                        body1.expressions.add(parseStatement());
                        forr.contents = body1;
                    });

        });
        if(exp.value != null) return exp.value;
        accept(WHILE).ifPresent(i -> {
            While whilee = new While();
            exp.value = whilee;
            consume(OPEN_PARENTHESIS);
            whilee.conditional = parseExpression();
            accept(OPEN_BRACE).ifPresentOrElse(ii ->
                            whilee.contents = parseBody(),
                    () -> {
                        Body body1 = new Body();
                        body1.expressions.add(parseStatement());
                        whilee.contents = body1;
                    });
        });
        if(exp.value != null) return exp.value;
        accept(RETURN).ifPresent(i -> {
            Return returnn = new Return();
            accept(SEMICOLON).ifPresentOrElse(ii -> {},
                    () -> returnn.returnValue = parseExpression());
            exp.value = returnn;
        });
        if(exp.value != null) return exp.value;
        return parseSingleStatement();
    }

    public Expression parseSingleStatement(){
        var exp = new LambdaContainer<Expression>();
        accept(IDENTIFIER).ifPresentOrElse(i -> {
            if(findNext(ASSIGNMENT, MULT_ASSIGNMENT, DIV_ASSIGNMENT, ADD_ASSIGNMENT, SUB_ASSIGNMENT) < findNext(OPEN_PARENTHESIS, SEMICOLON, CLOSE_PARENTHESIS)){
                rewind(1);
                if(distanceTo(ASSIGNMENT, MULT_ASSIGNMENT, DIV_ASSIGNMENT, ADD_ASSIGNMENT, SUB_ASSIGNMENT) > 1){
                    exp.value = parseDeclaration();
                } else{
                    exp.value = parseAssignment();
                }
            }else{
                accept(IDENTIFIER).ifPresentOrElse(ii -> {
                    rewind(2);
                    exp.value = parseDeclaration();
                }, () -> {
                    rewind(1);
                    exp.value = parseExpression();
                });

            }
        }, () -> accept(SEMICOLON).ifPresentOrElse(i -> {}, this::fail));
        return exp.value;
    }

    public Expression parseExpression(){
        List<Expression> subexpressions = new ArrayList<>();
        while(!next(CLOSE_PARENTHESIS, SEMICOLON, COMMA, TERNARY_OR)){
            subexpressions.add(parseIndividualValue());

            LambdaContainer<TokenType> opcontainer = new LambdaContainer<>();

            accept(ASSIGNMENT, PLUS, MINUS, MULTIPLY, DIVIDE, LESS, LEQUAL, GEQUAL, GREATER, EQUALS, NOT_EQUALS, BOOL_AND, BOOL_OR, BIT_AND, BIT_OR)
                    .ifPresentOrElse(i -> {
                        opcontainer.value = i.type();
                        BinaryOp op = new BinaryOp();
                        op.op = i.contents();
                        subexpressions.add(op);
                    }, () -> accept(TERNARY_IF).ifPresentOrElse(ii -> {
                        TernaryOp op = new TernaryOp();
                        op.conditional = process(subexpressions);
                        subexpressions.clear();
                        op.then = parseExpression();
                        op.els = parseExpression();
                        rewind(1);
                        subexpressions.add(op);
                    }, () -> {
                    }));
        }

        consume(CLOSE_PARENTHESIS, SEMICOLON, COMMA, TERNARY_OR);

        if(subexpressions.size() == 0) return new EmptyExpression();

        if(subexpressions.size() == 1) return subexpressions.get(0);

        return process(subexpressions);
    }

    public Expression process(List<Expression> expressions){
        List<List<String>> chars = List.of(
                List.of("*", "/"),
                List.of("+", "-"),
                List.of("|", "&"),
                List.of("==", ">=", "<=", "<", ">", "!="),
                List.of("||", "&&"),
                List.of("="));

        for(List<String> charlist : chars){
            reset: while(true){
                for(int i = 0; i < expressions.size(); i++) {
                    var exp = expressions.get(i);
                    if (exp instanceof BinaryOp) {
                        for (var charr : charlist) {
                            if (((BinaryOp) exp).op.equals(charr)) {
                                if (((BinaryOp) exp).right == null) {
                                    ((BinaryOp) exp).left = expressions.get(i - 1);
                                    ((BinaryOp) exp).right = expressions.get(i + 1);
                                    expressions.set(i - 1, exp);
                                    expressions.remove(i + 1);
                                    expressions.remove(i);
                                    continue reset;
                                }

                            }
                        }
                    }
                }
                break;
            }
        }

        if(expressions.size() > 1) fail();

        return expressions.get(0);
    }

    private static boolean contains(List<TokenType> list, TokenType... vars){
        for(var e : list){
            for(var val : vars){
                if(e == val) return true;
            }
        }

        return false;
    }

    public Expression parseIndividualValue(){
        LambdaContainer<Expression> container = LambdaContainer.create();
        accept(OPEN_PARENTHESIS).ifPresent(i -> container.value = parseExpression());

        accept(IDENTIFIER).ifPresent(i -> accept(OPEN_PARENTHESIS).ifPresentOrElse(p -> {
            FunctionCall fcall = new FunctionCall();
            fcall.name = new Identifier(i.contents());
            while(!previous(CLOSE_PARENTHESIS)){
                fcall.args.expressions.add(parseExpression());
            }

            container.value = fcall;
        }, () -> container.value = new Identifier(i.contents())));

        accept(FLOAT_LITERAL).ifPresent(i -> container.value = new FloatLiteral(Float.parseFloat(i.contents())));

        accept(INT_LITERAL).ifPresent(i -> container.value = new IntegerLiteral(Integer.parseInt(i.contents())));

        accept(RETURN_ACCESSOR).ifPresent(i -> container.value = new FieldAccessor(new Identifier(i.contents()), container.value));

        accept(PLUSPLUS, MINUSMINUS).ifPresent(i -> {
            UnaryOp op = new UnaryOp();
            op.op = i.contents();
            op.exp = container.value;
            op.after = true;
            container.value = op;
        });
        accept(NEGATE).ifPresent(i -> {
            UnaryOp op = new UnaryOp();
            op.op = "-";
            op.exp = parseIndividualValue();
            container.value = op;
        });
        if(container.value == null) fail();

        return container.value;
    }

    public Assignment parseAssignment(){
        var identifiers = findNext(ASSIGNMENT, MULT_ASSIGNMENT, DIV_ASSIGNMENT, ADD_ASSIGNMENT, SUB_ASSIGNMENT, SEMICOLON, COMMA) - current;
        if(identifiers > 1){
            return parseDeclaration();
        }

        Assignment assignment = new Assignment();

        assignment.name = new Identifier(consume(IDENTIFIER).contents());

        var assigntype = consume(ASSIGNMENT, MULT_ASSIGNMENT, DIV_ASSIGNMENT, ADD_ASSIGNMENT, SUB_ASSIGNMENT);
        assignment.assigntype = assigntype.contents();
        assignment.value = parseExpression();

        return assignment;
    }

    public Declaration parseDeclaration(){
        Declaration declaration = new Declaration();
        while(!(next(ASSIGNMENT, SEMICOLON))){
            accept(LAYOUT).ifPresent(s -> declaration.modifiers.modifiers.add(parseLayout()));
            accept(IDENTIFIER, UNIFORM, IN, OUT, INOUT).ifPresent(s -> declaration.modifiers.modifiers.add(new Modifier(s.contents())));
        }

        if(declaration.modifiers.modifiers.get(declaration.modifiers.modifiers.size()-1).value.equals("in") ||
                declaration.modifiers.modifiers.get(declaration.modifiers.modifiers.size()-1).value.equals("out")){
            declaration.name = new Identifier("");
        }else{
            declaration.name = new Identifier(declaration.modifiers.modifiers.remove(declaration.modifiers.modifiers.size()-1).value);
        }
        declaration.type = new Identifier(declaration.modifiers.modifiers.remove(declaration.modifiers.modifiers.size()-1).value);

        if(accept(SEMICOLON).isPresent()) return declaration;
        consume(ASSIGNMENT);
        declaration.assigntype = "=";

        declaration.value = parseExpression();
        return declaration;
    }

    public Struct parseStruct(){
        Struct struct = new Struct();
        while(!next(STRUCT)){
            accept(LAYOUT).ifPresent(s -> struct.modifiers.modifiers.add(parseLayout()));
            accept(IDENTIFIER).ifPresent(s -> struct.modifiers.modifiers.add(new Modifier(s.contents())));
        }

        consume(STRUCT);
        struct.name = new Identifier(consume(IDENTIFIER).contents());

        consume(OPEN_BRACE);
        while(!next(CLOSE_BRACE)){
            struct.declarations.expressions.add(parseDeclaration());
        }

        consume(CLOSE_BRACE);

        accept(IDENTIFIER).ifPresentOrElse(i -> {
            struct.variable = new Identifier(i.contents());
            consume(SEMICOLON);
        }, () -> consume(SEMICOLON));

        return struct;
    }

    public Interface parseInterface(){
        Interface interfacee = new Interface();
        interfacee.modifiers = new Modifiers();
        while(!next(UNIFORM, IN, OUT, INOUT)){
            accept(LAYOUT).ifPresent(s -> interfacee.modifiers.modifiers.add(parseLayout()));
            accept(IDENTIFIER).ifPresent(s -> interfacee.modifiers.modifiers.add(new Modifier(s.contents())));
        }

        interfacee.accessor = new Identifier(consume(UNIFORM, IN, OUT, INOUT).contents());
        interfacee.name = new Identifier(consume(IDENTIFIER).contents());

        consume(OPEN_BRACE);
        while(!next(CLOSE_BRACE)){
            interfacee.declarations.expressions.add(parseDeclaration());
        }

        consume(CLOSE_BRACE);

        accept(IDENTIFIER).ifPresentOrElse(i -> {
                interfacee.variable = new Identifier(i.contents());
                consume(SEMICOLON);
        }, () -> consume(SEMICOLON));

        return interfacee;
    }

    public Layout parseLayout(){
        List<Expression> layoutValues = new ArrayList<>();
        consume(OPEN_PARENTHESIS);

        do {
            if(findNext(ASSIGNMENT) < findNext(COMMA, CLOSE_PARENTHESIS))
                layoutValues.add(parseExpression());
            else {
                layoutValues.add(parseIndividualValue());
                consume(CLOSE_PARENTHESIS, COMMA);
            }
        } while (!previous(CLOSE_PARENTHESIS));

        return new Layout(layoutValues);

    }

    public void setIndex(int current){
        this.current = current;
    }

    public Lexer.Token consume(TokenType... tokenTypes){
        if(tokenTypes.length == 0){
            var value = lexer.getContents().get(current);
            current++;
            return value;
        }
        for(TokenType tokenType : tokenTypes){
            if(lexer.getContents().get(current).type() == tokenType){
                var value = lexer.getContents().get(current);
                current++;
                return value;
            }
        }
        throw new ShaderException("Failed to consume token at position " + current
                + ": token assigntype is " + lexer.getContents().get(current).type().name()
                + ", expected one of " + Arrays.stream(tokenTypes).map(tokenType -> tokenType.name() + " ").collect(Collectors.joining()));
    }

    public Optional<Lexer.Token> accept(TokenType... tokenTypes){
        for(TokenType tokenType : tokenTypes){
            if(lexer.getContents().get(current).type() == tokenType){
                var value = lexer.getContents().get(current);
                current++;
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }

    public boolean next(TokenType... tokenTypes){
        for(TokenType tokenType : tokenTypes){
            if(lexer.getContents().get(current).type().equals(tokenType)){
                return true;
            }
        }

        return false;
    }

    public boolean previous(TokenType... tokenTypes){
        for(TokenType tokenType : tokenTypes){
            if(lexer.getContents().get(current - 1).type() == tokenType){
                return true;
            }
        }

        return false;
    }

    public int findNext(TokenType... tokenTypes){
       int smallest = Integer.MAX_VALUE;
       for(TokenType tokenType : tokenTypes){
           for(int i = current; i < lexer.getContents().size(); i++){
               if(lexer.getContents().get(i).type() == tokenType){
                   if(i < smallest) smallest = i;
               }
           }
       }

        return smallest;
    }

    public int distanceTo(TokenType... tokenTypes){
        int next = findNext(tokenTypes);
        return next - current;
    }

    public void rewind(int amount){
        current = Math.max(0, current-amount);
    }

    public void fail(){
        System.out.println(lexer.getContents());
        throw new ShaderException("Failed parsing: current token is " + lexer.getContents().get(current) + " at token " + current);
    }

    public static class AbstractSyntaxTree {
        public List<Node> nodes = new ArrayList<>();

        public void printTree(){
            for(Node node : nodes){
                recursivePrint(0, node);
            }
        }

        private void recursivePrint(int level, Node node){
            StringBuilder value = new StringBuilder();
            for(int i = 0; i < level; i++) value.append("   ");
            value.append(node.toString());

            System.out.println(value + " (" + node.getClass().getSimpleName() + ")");
            for(Node node2 : node.getChildren()){
                if(node2 != null) recursivePrint(level + 1, node2);
            }
        }

        public List<Node> getAll(){
            var list = new ArrayList<Node>();
            for(var node : nodes){
                list.addAll(recursiveGet(node));
            }

            return list;
        }

        private List<Node> recursiveGet(Node node){
            var list = new ArrayList<Node>();
            list.add(node);
            for(Node node2 : node.getChildren()){
                if(node2 != null) list.addAll(recursiveGet(node2));
            }

            return list;
        }
    }

    public abstract static class Expression extends Node{}

    public abstract static class Node{
        public abstract List<Node> getChildren();
    }

    public static class If extends Node{
        public Expression conditional;
        public Body then;
        public Body els = new Body();

        @Override
        public List<Node> getChildren() {
            return List.of(conditional, then, els);
        }
    }

    public static class For extends Node{
        public Expression assignment;
        public Expression conditional;
        public Expression modifier;
        public Body contents;

        @Override
        public List<Node> getChildren() {
            return List.of(assignment, conditional, modifier, contents);
        }

        @Override
        public String toString(){
            return "for {" + assignment + "} {" + conditional + "} {" + modifier + "}:";
        }
    }

    public static class While extends Node{
        public Expression conditional;
        public Body contents;


        @Override
        public List<Node> getChildren() {
            return List.of(conditional, contents);
        }
    }

    public static class Body extends Node{
        public List<Node> expressions = new ArrayList<>();

        @Override
        public List<Node> getChildren() {
            return expressions;
        }

        @Override
        public String toString() {
            return "Body (" + expressions.size() + " lines):";
        }
    }

    public static class Identifier extends Expression{
        public String value;

        public Identifier(String value) {
            this.value = value;
        }

        @Override public String toString() { return value;}

        @Override
        public List<Node> getChildren() {
            return new ArrayList<>();
        }
    }

    public static class Modifier extends Expression{
        public String value;

        public Modifier(String value) {
            this.value = value;
        }

        @Override public String toString() { return value;}

        @Override
        public List<Node> getChildren() {
            return new ArrayList<>();
        }
    }

    public static class Layout extends Modifier{
        public List<Expression> expressions;

        public Layout(List<Expression> expressions){
            super("layout(" + expressions.stream().map(Expression::toString).collect(Collectors.joining(", ")) + ")");
            this.expressions = expressions;
        }

        @Override
        public List<Node> getChildren() {
            return List.copyOf(expressions);
        }
    }

    public static class Arguments extends Node{
        public List<Expression> expressions = new ArrayList<>();

        @Override
        public List<Node> getChildren() {
            return List.copyOf(expressions);
        }

        @Override
        public String toString(){
            return expressions.toString();
        }
    }

    public static class FloatLiteral extends Expression{
        public float value;

        public FloatLiteral(float value) {
            this.value = value;
        }

        @Override
        public String toString(){
            return String.valueOf(value);
        }

        @Override
        public List<Node> getChildren() {
            return new ArrayList<>();
        }
    }

    public static class IntegerLiteral extends Expression{
        public int value;

        public IntegerLiteral(int value) {
            this.value = value;
        }

        @Override
        public List<Node> getChildren() {
            return new ArrayList<>();
        }

        @Override
        public String toString(){
            return Integer.toString(value);
        }
    }

    public static class AccessModifier extends Identifier{
        public AccessModifier(String value) {
            super(value);
        }
    }

    public static class FieldAccessor extends Expression{
        Identifier accessor;
        Expression value;

        public FieldAccessor(Identifier accessor, Expression value) {
            this.accessor = accessor;
            this.value = value;
        }

        @Override
        public List<Node> getChildren() {
            return List.of(value, accessor);
        }

        @Override
        public String toString(){
            return "Accessing field " + accessor + " of " + value;
        }

    }

    public static class FunctionCall extends Expression{
        Identifier name;
        Arguments args = new Arguments();

        @Override
        public List<Node> getChildren() {
            return List.of(name, args);
        }

        @Override
        public String toString() {
            return "Calling " + name + " " + args;
        }
    }

    public static class Modifiers extends Node{
        public List<Modifier> modifiers = new ArrayList<>();

        @Override
        public String toString() {
            return modifiers.toString();
        }

        @Override
        public List<Node> getChildren() {
            return List.copyOf(modifiers);
        }
    }

    public static class DeclarationArguments extends Node{
        List<Declaration> args = new ArrayList<>();

        @Override
        public List<Node> getChildren() {
            return List.copyOf(args);
        }
    }

    public static class EmptyExpression extends Expression{

        @Override
        public String toString(){
            return "";
        }

        @Override
        public List<Node> getChildren() {
            return List.of();
        }
    }

    public static class Return extends Expression{
        Expression returnValue;

        @Override
        public List<Node> getChildren() {
            return List.of(returnValue);
        }

        @Override
        public String toString(){
            return "Return [" + returnValue + "]";
        }

    }

    public static class LoopControl extends Expression{
        String value;


        @Override
        public List<Node> getChildren() {
            return List.of();
        }
    }

    public static class Function extends Node{
        Modifiers modifiers = new Modifiers();
        Identifier type;
        Identifier name;
        DeclarationArguments args = new DeclarationArguments();
        Body body;

        @Override
        public List<Node> getChildren() {
            return List.of(modifiers, type, name, args, body);
        }

        @Override
        public String toString(){
            return "Function def " + modifiers + " [" + type + "] [" + name + "]";
        }
    }

    public static class Struct extends Node{
        Modifiers modifiers= new Modifiers();
        public Identifier name = new Identifier("");
        public Body declarations = new Body();
        public Identifier variable = new Identifier("");

        @Override
        public String toString(){
            return "struct " + modifiers.toString() + " " + name + " " + variable;
        }

        @Override
        public List<Node> getChildren() {
            return List.of(modifiers, name, declarations, variable);
        }
    }

    public static class Interface extends Node{
        public Modifiers modifiers= new Modifiers();
        public Identifier accessor = new Identifier("");
        public Identifier name = new Identifier("");
        public Body declarations = new Body();
        public Identifier variable = new Identifier("");

        @Override
        public String toString(){
            return "interface " + accessor.toString() + " " + modifiers.toString() + " " + name + " " + variable;
        }

        @Override
        public List<Node> getChildren() {
            return List.of(modifiers, accessor, name, declarations, variable);
        }
    }

    public static class UnaryOp extends Expression{
        Expression exp;
        String op;
        boolean after;

        @Override
        public List<Node> getChildren() {
            return List.of(exp);
        }

        @Override
        public String toString(){
            return op;
        }
    }

    public static class BinaryOp extends Expression{
        public Expression left;
        public Expression right;
        public String op;

        public BinaryOp(Expression left, Expression right, String op) {
            this.left = left;
            this.right = right;
            this.op = op;
        }

        public BinaryOp() {
        }

        @Override
        public String toString(){
            return String.valueOf(op);
        }

        @Override
        public List<Node> getChildren() {
            return List.of(left, right);
        }
    }

    public static class TernaryOp extends Expression{
        Expression conditional;
        Expression then;
        Expression els;

        @Override
        public List<Node> getChildren() {
            return List.of(conditional, then, els);
        }
    }

    public static class Assignment extends Expression{
        public Identifier name;
        public String assigntype;
        Expression value = new EmptyExpression();

        public Assignment() {
        }

        public Assignment(Identifier name, String assigntype, Expression value) {
            this.name = name;
            this.assigntype = assigntype;
            this.value = value;
        }

        @Override
        public List<Node> getChildren() {
            return List.of(name, value);
        }

        @Override
        public String toString(){
            return "=";
        }
    }

    public static class Declaration extends Assignment{
        public Modifiers modifiers = new Modifiers();
        public Identifier type;

        @Override
        public String toString(){
            return modifiers.toString() + " " + type + " " + name;
        }

        @Override
        public List<Node> getChildren() {
            return List.of(modifiers, type, name, value);
        }
    }
}
