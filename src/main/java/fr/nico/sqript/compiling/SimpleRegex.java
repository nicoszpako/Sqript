package fr.nico.sqript.compiling;

import java.util.*;

public class SimpleRegex {

    public static String simplePatternToRegex(String simplePattern, boolean canBeOptional) throws Exception {
        //System.out.println("Converted "+simplePattern+" as "+result);
        return convert(canBeOptional? compileOptional(treefy(simplePattern)) : compileNonOptional(treefy(simplePattern)));
    }

    private static boolean allOptional(Tree<PatternToken> tree){
        if(tree.label == null || tree.label.tokenType == EnumTokenType.PARENTHESIS){
            boolean result = true;
            for (Tree<PatternToken> child : tree.children) {
                result = result && allOptional(child);
            }
            return result;
        }else {
            return labelIsOptional(tree.label);
        }
    }

    private static boolean labelIsOptional(PatternToken label) {
        switch(label.tokenType){
            case PARENTHESIS:
            case CHAR:
                return false;
            case SPACE:
            case BRACKETS:
                return true;
        }
        return false;
    }


    static Tree<PatternToken> compileNonOptional(Tree<PatternToken> tree) throws PatternAllOptionalException {
        if(allOptional(tree))
            throw new PatternAllOptionalException();
        return compile(tree,true,0);
    }

    static Tree<PatternToken> compileOptional(Tree<PatternToken> tree) {
        return compile(tree,true,0);
    }

    static Tree<PatternToken> compile(Tree<PatternToken> tree,boolean parentPrecedentIsOptional,int indent) {

        //String tab = "";
        //for (int i = 0; i < indent; i++) {
        //    tab+="\t";
        //}
        boolean precedentIsOptional = true;
        //System.out.println(tab+"Compiling : "+tree);
        Tree<PatternToken> result = new Tree<PatternToken>();
        ListIterator<Tree<PatternToken>> i = tree.children.listIterator();
        // Looping over all the tree's children
        while (i.hasNext()) {

            Tree<PatternToken> current = i.next();
            //System.out.println(tab+"Current : "+current+" , precedentIsOptional : "+precedentIsOptional+", parentPrecedentIsOptional : "+parentPrecedentIsOptional);
            if (current.label != null && current.label.tokenType == EnumTokenType.SPACE) {
                if (i.hasPrevious() && i.previousIndex() >= 1 && tree.children.get(i.previousIndex()-1).label.tokenType == EnumTokenType.BRACKETS) {
                    //If left-adjacent to a char
                    if(i.previousIndex()-1>=1){
                        if(tree.children.get(i.previousIndex()-1).label.tokenType == EnumTokenType.CHAR){
                            continue;
                        }
                    }
                    if(precedentIsOptional || i.previousIndex() == 1){
                        if(!precedentIsOptional || !parentPrecedentIsOptional)
                            continue;
                        tree.children.get(i.previousIndex()-1).add(current);
                        i.remove();
                        //System.out.println(tab+"Shifted pre : "+convert(tree));

                        continue;
                    }
                }
                if (i.hasNext() && tree.children.get(i.nextIndex()).label.tokenType == EnumTokenType.BRACKETS) {
                    //If right-adjacent to a char
                    if(tree.children.size() > i.nextIndex()+1){
                        if(tree.children.get(i.nextIndex()+1).label.tokenType == EnumTokenType.CHAR){
                            continue;
                        }
                    }
                    tree.children.get(i.nextIndex()).add(0, current);
                    i.remove();
                    //System.out.println(tab+"Shifted post : "+convert(tree));

                }
            }else{
                if(current.label == null || current.label.tokenType != EnumTokenType.CHAR)
                    i.set(compile(current,parentPrecedentIsOptional && precedentIsOptional,indent+1));
                precedentIsOptional = precedentIsOptional && allOptional(current);
            }

        }
        //System.out.println(tab+"Result : "+convert(tree));
        return tree;
    }

    static String convert(Tree<PatternToken> tree) {
        StringBuilder result = new StringBuilder();
        if(tree.label == null){
            for (Tree<PatternToken> alternatives : tree.children) {
                result.append(convert(alternatives));
            }
        }else switch (tree.label.tokenType) {
            case SPACE:
                result.append(' ');
                break;
            case CHAR:
                result.append(tree.label.content);
                break;
            case PARENTHESIS:
                result.append("(?").append(tree.label.groupName == null ? ":" : "<m" + tree.label.groupName + ">");
                for (Tree<PatternToken> alternatives : tree.children) {
                    result.append(convert(alternatives)).append('|');
                }
                result.deleteCharAt(result.length() - 1).append(")");
                break;
            case BRACKETS:
                result.append("(?:");
                for (Tree<PatternToken> alternatives : tree.children) {
                    result.append(convert(alternatives));
                }
                result.append(")?");
                break;
        }
        return result.toString();
    }

    static Tree<PatternToken> treefy(String pattern) throws Exception {
        int c = 0;
        Stack<Tree<PatternToken>> treeStack = new Stack<>();
        treeStack.add(new Tree<>());
        boolean skip = false;
        while (c < pattern.length()) {
            if(pattern.charAt(c) == '{') {
                skip = true;
            } else if (pattern.charAt(c) == '}'){
                skip = false;
            }
            if (!skip) {
                if (pattern.charAt(c) == '~'){
                    c++;
                    treeStack.peek().add(new Tree<>(new PatternToken(EnumTokenType.CHAR, '\\')));
                    treeStack.peek().add(new Tree<>(new PatternToken(EnumTokenType.CHAR, pattern.charAt(c))));
                } else if (pattern.charAt(c) == '[') {
                    treeStack.add(new Tree<>(new PatternToken(EnumTokenType.BRACKETS)));
                } else if (pattern.charAt(c) == '(') {
                    treeStack.add(new Tree<>(new PatternToken(EnumTokenType.PARENTHESIS)));
                    treeStack.add(new Tree<>());
                } else if (pattern.charAt(c) == '|') {
                    Tree<PatternToken> top = treeStack.pop();
                    if (top == null || top.label != null)
                        throw new Exception("Bad use of '|' at " + c + " in : " + pattern);
                    treeStack.peek().add(top);
                    treeStack.add(new Tree<>());
                } else if (pattern.charAt(c) == ']') {
                    Tree<PatternToken> top = treeStack.pop();
                    if (top == null || top.label == null || !(top.label.tokenType == EnumTokenType.BRACKETS))
                        throw new Exception("Bad use of brackets at " + c + " in : " + pattern);
                    treeStack.peek().add(top);
                } else if (pattern.charAt(c) == ')') {
                    Tree<PatternToken> top = treeStack.pop();
                    treeStack.peek().add(top);
                    top = treeStack.pop();
                    if (top == null || top.label == null || !(top.label.tokenType == EnumTokenType.PARENTHESIS))
                        throw new Exception("Bad use of parenthesis at " + c + " in : " + pattern);
                    treeStack.peek().add(top);
                } else if (pattern.charAt(c) == ' ') {
                    treeStack.peek().add(new Tree<>(new PatternToken(EnumTokenType.SPACE)));
                } else if (pattern.charAt(c) ==';'){
                    StringBuilder groupName = new StringBuilder();
                    while(!treeStack.peek().children.isEmpty()){
                        groupName.append(convert(treeStack.peek().children.remove(treeStack.peek().children.size()-1)));
                    }
                    //Stack top is : ..... | PARENTHESIS | ALTERNATIVE so in order to get PARENTHESIS tree we get (n-2)th element
                    treeStack.get(treeStack.size()-2).label.groupName = groupName.toString();
                } else
                    treeStack.peek().add(new Tree<>(new PatternToken(EnumTokenType.CHAR, pattern.charAt(c))));
            }else{
                treeStack.peek().add(new Tree<>(new PatternToken(EnumTokenType.CHAR, pattern.charAt(c))));
            }
            c++;
        }
        if (treeStack.size() == 1)
            return treeStack.pop();
        else throw new Exception("Bracket or parenthesis error in : " + pattern);
    }


    public static class PatternAllOptionalException extends Exception {    }
    public static class BadUseOfBracketException extends Exception {    }

    public static class Tree<T> {

        private List<Tree<T>> children = new ArrayList<>();
        private T label;

        public Tree(List<Tree<T>> children) {
            this.children = children;
        }

        public Tree() {
        }

        public Tree(T label) {
            this.label = label;
        }

        public Tree(T label, List<Tree<T>> children) {
            this.children = children;
            this.label = label;
        }

        public List<Tree<T>> getChildren() {
            return children;
        }

        public void setChildren(List<Tree<T>> children) {
            this.children = children;
        }

        public void add(Tree<T> tree) {
            children.add(tree);
        }

        public void add(int index, Tree<T> tree) {
            children.add(index, tree);
        }

        public T getLabel() {
            return label;
        }

        public void setLabel(T label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return "[ " + label + (children.isEmpty() ? "" : " -> " + children) + " ]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tree<?> tree = (Tree<?>) o;
            return Objects.equals(children, tree.children) && Objects.equals(label, tree.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(children, label);
        }
    }

    public enum EnumTokenType {
        BRACKETS,
        PARENTHESIS,
        SPACE,
        CHAR
    }

    public static class PatternToken {

        private EnumTokenType tokenType;
        private char content;
        private String groupName;

        public PatternToken(EnumTokenType patternType) {
            this.tokenType = patternType;
        }

        public PatternToken(EnumTokenType patternType, char content) {
            this.tokenType = patternType;
            this.content = content;
        }

        public EnumTokenType getTokenType() {
            return tokenType;
        }

        public void setTokenType(EnumTokenType tokenType) {
            this.tokenType = tokenType;
        }

        public char getContent() {
            return content;
        }

        public void setContent(char content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return tokenType +
                    (content == 0 ? "" : " '" + content + "'");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PatternToken that = (PatternToken) o;
            return tokenType == that.tokenType && Objects.equals(content, that.content);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tokenType, content);
        }
    }


}
