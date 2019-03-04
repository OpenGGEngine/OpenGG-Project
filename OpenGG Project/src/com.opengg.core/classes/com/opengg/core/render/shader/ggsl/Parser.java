package com.opengg.core.render.shader.ggsl;

import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.math.Tuple;
import com.opengg.core.util.LambdaContainer;

import java.util.*;
import java.util.stream.Collectors;

import static com.opengg.core.render.shader.ggsl.Token.*;

public class Parser {

    private Lexer lexer;
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
        var identifierStack = new ArrayDeque<String>();
        while(!(next(OPEN_PARENTHESIS))){
            identifierStack.push(consume(IDENTIFIER).y);
        }

        function.name = new Identifier(identifierStack.pop());
        function.type = new Identifier(identifierStack.pop());
        function.modifiers.modifiers = identifierStack.stream().map(Identifier::new).collect(Collectors.toList());

        consume(OPEN_PARENTHESIS);

        while(!(next(CLOSE_PARENTHESIS))){
            Declaration declaration = new Declaration();

            identifierStack = new ArrayDeque<String>();
            while(!(next(COMMA) || next(CLOSE_PARENTHESIS))){
                identifierStack.push(consume(IDENTIFIER, UNIFORM).y);
            }

            declaration.name = new Identifier(identifierStack.pop());
            declaration.type = new Identifier(identifierStack.pop());
            declaration.modifiers.modifiers = identifierStack.stream().map(Identifier::new).collect(Collectors.toList());

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
            accept(OPEN_BRACE).ifPresentOrElse(o -> {
                iff.then = parseBody();
            }, () -> {
                Body body2 = new Body();
                body2.expressions.add(parseStatement());
                iff.then = body2;
            });

            accept(ELSE).ifPresent(e -> {
                accept(OPEN_BRACE).ifPresentOrElse(o -> {
                    iff.els = parseBody();
                }, () -> {
                    Body body2 = new Body();
                    body2.expressions.add(parseStatement());
                    iff.els = body2;
                });
            });
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
                    () -> {
                        returnn.returnValue = parseExpression();
                    });
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

            LambdaContainer<Token> opcontainer = new LambdaContainer<>();

            accept(PLUS, MINUS, MULTIPLY, DIVIDE, LESS, LEQUAL, GEQUAL, GREATER, EQUALS, NOT_EQUALS, BOOL_AND, BOOL_OR, BIT_AND, BIT_OR)
                    .ifPresentOrElse(i -> {
                        opcontainer.value = i.x;
                        BinaryOp op = new BinaryOp();
                        op.op = i.y;
                        subexpressions.add(op);
                    }, () -> {
                        accept(TERNARY_IF).ifPresentOrElse(ii -> {
                            TernaryOp op = new TernaryOp();
                            op.conditional = process(subexpressions);
                            subexpressions.clear();
                            op.then = parseExpression();
                            op.els = parseExpression();
                            rewind(1);
                            subexpressions.add(op);
                        }, () -> {
                        });

                    });
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
                List.of("||", "&&"));

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

    private static boolean contains(List<Token> list, Token... vars){
        for(var e : list){
            for(var val : vars){
                if(e == val) return true;
            }
        }

        return false;
    }

    public Expression parseIndividualValue(){
        LambdaContainer<Expression> container = LambdaContainer.create();

        accept(OPEN_PARENTHESIS).ifPresent(i -> {
            container.value = parseExpression();
        });

        accept(IDENTIFIER).ifPresent(i -> {
            accept(OPEN_PARENTHESIS).ifPresentOrElse(p -> {
                FunctionCall fcall = new FunctionCall();
                fcall.name = new Identifier(i.y);
                while(!previous(CLOSE_PARENTHESIS)){
                    fcall.args.expressions.add(parseExpression());
                }

                container.value = fcall;
            }, () -> {
                container.value = new Identifier(i.y);
            });
        });

        accept(FLOAT_LITERAL).ifPresent(i -> container.value = new FloatLiteral(Float.parseFloat(i.y)));

        accept(INT_LITERAL).ifPresent(i -> container.value = new IntegerLiteral(Integer.parseInt(i.y)));

        accept(RETURN_ACCESSOR).ifPresent(i -> container.value = new FieldAccessor(new Identifier(i.y), container.value));

        accept(PLUSPLUS, MINUSMINUS).ifPresent(i -> {
            UnaryOp op = new UnaryOp();
            op.op = i.y;
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

        assignment.name = new Identifier(consume(IDENTIFIER).y);

        var assigntype = consume(ASSIGNMENT, MULT_ASSIGNMENT, DIV_ASSIGNMENT, ADD_ASSIGNMENT, SUB_ASSIGNMENT);
        assignment.assigntype = assigntype.y;
        assignment.value = parseExpression();

        return assignment;
    }

    public Declaration parseDeclaration(){
        Declaration declaration = new Declaration();
        while(!(next(ASSIGNMENT, SEMICOLON))){
            accept(LAYOUT).ifPresent(s -> declaration.modifiers.modifiers.add(parseLayout()));
            accept(IDENTIFIER, UNIFORM, IN, OUT, INOUT).ifPresent(s -> declaration.modifiers.modifiers.add(new Identifier(s.y)));
        }

        declaration.name = declaration.modifiers.modifiers.remove(declaration.modifiers.modifiers.size()-1);
        declaration.type = declaration.modifiers.modifiers.remove(declaration.modifiers.modifiers.size()-1);
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
            accept(IDENTIFIER).ifPresent(s -> struct.modifiers.modifiers.add(new Identifier(s.y)));
        }

        consume(STRUCT);
        struct.name = new Identifier(consume(IDENTIFIER).y);

        consume(OPEN_BRACE);
        while(!next(CLOSE_BRACE)){
            struct.declarations.expressions.add(parseDeclaration());
        }

        consume(CLOSE_BRACE);

        accept(IDENTIFIER).ifPresentOrElse(i -> {
            struct.variable = new Identifier(i.y);
            consume(SEMICOLON);
        }, () -> consume(SEMICOLON));

        return struct;
    }

    public Interface parseInterface(){
        Interface interfacee = new Interface();
        interfacee.modifiers = new Modifiers();
        while(!next(UNIFORM, IN, OUT, INOUT)){
            accept(LAYOUT).ifPresent(s -> interfacee.modifiers.modifiers.add(parseLayout()));
            accept(IDENTIFIER).ifPresent(s -> interfacee.modifiers.modifiers.add(new Identifier(s.y)));
        }

        interfacee.accessor = new Identifier(consume(UNIFORM, IN, OUT, INOUT).y);
        interfacee.name = new Identifier(consume(IDENTIFIER).y);

        consume(OPEN_BRACE);
        while(!next(CLOSE_BRACE)){
            interfacee.declarations.expressions.add(parseDeclaration());
        }

        consume(CLOSE_BRACE);

        accept(IDENTIFIER).ifPresentOrElse(i -> {
                interfacee.variable = new Identifier(i.y);
                consume(SEMICOLON);
        }, () -> consume(SEMICOLON));

        return interfacee;
    }

    public Identifier parseLayout(){
        String layout = "layout(";
        consume(OPEN_PARENTHESIS);

        do {
            layout = layout + consume().y;
        } while (!next(CLOSE_PARENTHESIS));

        layout = layout + ")";
        consume(CLOSE_PARENTHESIS);
        return new Identifier(layout);

    }

    public void setIndex(int current){
        this.current = current;
    }

    public Tuple<Token, String> consume(Token... tokens){
        if(tokens.length == 0){
            var value = lexer.getContents().get(current);
            current++;
            return value;
        }
        for(Token token : tokens){
            if(lexer.getContents().get(current).x == token){
                var value = lexer.getContents().get(current);
                current++;
                return value;
            }
        }
        throw new ShaderException("Failed to consume token at position " + current
                + ": token assigntype is " + lexer.getContents().get(current).x.name()
                + ", expected one of " + Arrays.stream(tokens).map(token -> token.name() + " ").collect(Collectors.joining()));
    }

    public Optional<Tuple<Token, String>> accept(Token... tokens){
        for(Token token : tokens){
            if(lexer.getContents().get(current).x == token){
                var value = lexer.getContents().get(current);
                current++;
                return Optional.of(value);
            }
        }

        return Optional.ofNullable(null);
    }

    public boolean next(Token... tokens){
        for(Token token : tokens){
            if(lexer.getContents().get(current).x.equals(token)){
                return true;
            }
        }

        return false;
    }

    public boolean previous(Token... tokens){
        for(Token token : tokens){
            if(lexer.getContents().get(current - 1).x == token){
                return true;
            }
        }

        return false;
    }

    public int findNext(Token... tokens){
       int smallest = Integer.MAX_VALUE;
       for(Token token : tokens){
           for(int i = current; i < lexer.getContents().size(); i++){
               if(lexer.getContents().get(i).x == token){
                   if(i < smallest) smallest = i;
               }
           }
       }

        return smallest;
    }

    public int distanceTo(Token... tokens){
        int next = findNext(tokens);
        return next - current;
    }

    public void rewind(int amount){
        current = Math.max(0, current-amount);
    }

    public void fail(){
        throw new ShaderException("Failed parsing: current token is " + lexer.getContents().get(current));
    }

    public class AbstractSyntaxTree {
        public List<Node> nodes = new ArrayList<>();

        public void printTree(){
            for(Node node : nodes){
                recursivePrint(0, node);
            }
        }

        private void recursivePrint(int level, Node node){
            String value = "";
            for(int i = 0; i < level; i++) value += "   ";
            value += node.toString();

            System.out.println(value);
            for(Node node2 : node.getChildren()){
                if(node2 != null) recursivePrint(level + 1, node2);
            }
        }
    }

    public abstract class Expression extends Node{}

    public abstract class Node{
        public abstract List<Node> getChildren();
    }

    public class If extends Node{
        public Expression conditional;
        public Body then;
        public Body els = new Body();

        @Override
        public List<Node> getChildren() {
            return List.of(conditional, then, els);
        }
    }

    public class For extends Node{
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

    public class While extends Node{
        public Expression conditional;
        public Body contents;


        @Override
        public List<Node> getChildren() {
            return List.of(conditional, contents);
        }
    }

    public class Body extends Node{
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

    public class Identifier extends Expression{
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

    public class Arguments extends Node{
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

    public class FloatLiteral extends Expression{
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

    public class IntegerLiteral extends Expression{
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

    public class AccessModifier extends Identifier{
        public AccessModifier(String value) {
            super(value);
        }
    }

    public class FieldAccessor extends Expression{
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

    public class FunctionCall extends Expression{
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

    public class Modifiers extends Node{
        List<Identifier> modifiers = new ArrayList<>();

        @Override
        public String toString() {
            return modifiers.toString();
        }

        @Override
        public List<Node> getChildren() {
            return List.copyOf(modifiers);
        }
    }

    public class DeclarationArguments extends Node{
        List<Declaration> args = new ArrayList<>();

        @Override
        public List<Node> getChildren() {
            return List.copyOf(args);
        }
    }

    public class EmptyExpression extends Expression{

        @Override
        public String toString(){
            return "";
        }

        @Override
        public List<Node> getChildren() {
            return List.of();
        }
    }

    public class Return extends Expression{
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

    public class LoopControl extends Expression{
        String value;


        @Override
        public List<Node> getChildren() {
            return List.of();
        }
    }

    public class Function extends Node{
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

    public class Struct extends Node{
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

    public class Interface extends Node{
        Modifiers modifiers= new Modifiers();
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

    public class UnaryOp extends Expression{
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

    public class BinaryOp extends Expression{
        Expression left;
        Expression right;
        String op;

        @Override
        public String toString(){
            return String.valueOf(op);
        }

        @Override
        public List<Node> getChildren() {
            return List.of(left, right);
        }
    }

    public class TernaryOp extends Expression{
        Expression conditional;
        Expression then;
        Expression els;

        @Override
        public List<Node> getChildren() {
            return List.of(conditional, then, els);
        }
    }

    public class Assignment extends Expression{
        Identifier name;
        String assigntype;
        Expression value = new EmptyExpression();


        @Override
        public List<Node> getChildren() {
            return List.of(name, value);
        }

        @Override
        public String toString(){
            return "Assigning " + assigntype + " to " + name;
        }
    }

    public class Declaration extends Assignment{
        Modifiers modifiers = new Modifiers();
        Identifier type;

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
