package fr.nico.sqript.compiling;

import java.util.*;

public class SimpleRegex {

    public static String simplePatternToRegex(String simplePattern) throws Exception {
        String result = convert(compile(treefy(simplePattern)));
        //System.out.println("Converted "+simplePattern+" as "+result);
        return result;
    }

    private static boolean optional(Tree<PatternLabel> tree){
        if(tree.label == null || tree.label.patternType == EnumPatternType.PARENTHESIS){
            boolean result = true;
            for (Tree<PatternLabel> child : tree.children) {
                result = result && optional(child);
            }
            return result;
        }else {
            switch(tree.label.patternType){
                case SPACE:
                case CHAR:
                    return false;
                case BRACKETS:
                    return true;
            }
        }
        return false;
    }

    private static Tree<PatternLabel> compile(Tree<PatternLabel> tree) {
        ListIterator<Tree<PatternLabel>> i = tree.children.listIterator();
        while (i.hasNext()) {
            Tree<PatternLabel> current = i.next();
            if (current.label != null && current.label.patternType == EnumPatternType.SPACE) {
                if (i.hasNext() && tree.children.get(i.nextIndex()).label.patternType == EnumPatternType.BRACKETS) {
                    tree.children.get(i.nextIndex()).add(0, current);
                    i.remove();
                }else if (i.hasPrevious() && i.previousIndex() >= 1 && tree.children.get(i.previousIndex()-1).label.patternType == EnumPatternType.BRACKETS) {
                    boolean allOptional = true;
                    for (int j = 0; j < i.previousIndex()-1; j++) {
                        allOptional = allOptional && optional(tree.children.get(j));
                    }
                    if(allOptional){
                        tree.children.get(i.previousIndex()-1).add(current);
                        i.remove();
                    }
                }
            }
        }
        for (int j = 0; j < tree.children.size(); j++) {
            tree.children.set(j, compile(tree.children.get(j)));
        }
        return tree;
    }

    private static String convert(Tree<PatternLabel> tree) {
        StringBuilder result = new StringBuilder();
        if(tree.label == null){
            for (Tree<PatternLabel> alternatives : tree.children) {
                result.append(convert(alternatives));
            }
        }else switch (tree.label.patternType) {
            case SPACE:
                result.append(' ');
                break;
            case CHAR:
                result.append(tree.label.content);
                break;
            case PARENTHESIS:
                result.append("(?").append(tree.label.groupName == null ? ":" : "<m" + tree.label.groupName + ">");
                for (Tree<PatternLabel> alternatives : tree.children) {
                    result.append(convert(alternatives)).append('|');
                }
                result.deleteCharAt(result.length() - 1).append(")");
                break;
            case BRACKETS:
                result.append("(?:");
                for (Tree<PatternLabel> alternatives : tree.children) {
                    result.append(convert(alternatives));
                }
                result.append(")?");
                break;
        }
        return result.toString();
    }

    private static Tree<PatternLabel> treefy(String s1) throws Exception {
        int c = 0;
        Stack<Tree<PatternLabel>> treeStack = new Stack<>();
        treeStack.add(new Tree<>());
        boolean skip = false;
        while (c < s1.length()) {
            if(s1.charAt(c) == '{') {
                skip = true;
            } else if (s1.charAt(c) == '}'){
                skip = false;
            }
            if (!skip) {
                if (s1.charAt(c) == '~'){
                    c++;
                    treeStack.peek().add(new Tree<>(new PatternLabel(EnumPatternType.CHAR, '\\')));
                    treeStack.peek().add(new Tree<>(new PatternLabel(EnumPatternType.CHAR, s1.charAt(c))));
                } else if (s1.charAt(c) == '[') {
                    treeStack.add(new Tree<>(new PatternLabel(EnumPatternType.BRACKETS)));
                } else if (s1.charAt(c) == '(') {
                    treeStack.add(new Tree<>(new PatternLabel(EnumPatternType.PARENTHESIS)));
                    treeStack.add(new Tree<>());
                } else if (s1.charAt(c) == '|') {
                    Tree<PatternLabel> top = treeStack.pop();
                    if (top == null || top.label != null)
                        throw new Exception("Bad use of '|' at " + c + " in : " + s1);
                    treeStack.peek().add(top);
                    treeStack.add(new Tree<>());
                } else if (s1.charAt(c) == ']') {
                    Tree<PatternLabel> top = treeStack.pop();
                    if (top == null || top.label == null || !(top.label.patternType == EnumPatternType.BRACKETS))
                        throw new Exception("Bad use of brackets at " + c + " in : " + s1);
                    treeStack.peek().add(top);
                } else if (s1.charAt(c) == ')') {
                    Tree<PatternLabel> top = treeStack.pop();
                    treeStack.peek().add(top);
                    top = treeStack.pop();
                    if (top == null || top.label == null || !(top.label.patternType == EnumPatternType.PARENTHESIS))
                        throw new Exception("Bad use of parenthesis at " + c + " in : " + s1);
                    treeStack.peek().add(top);
                } else if (s1.charAt(c) == ' ') {
                    treeStack.peek().add(new Tree<>(new PatternLabel(EnumPatternType.SPACE)));
                } else if (s1.charAt(c) ==';'){
                    StringBuilder groupName = new StringBuilder();
                    while(!treeStack.peek().children.isEmpty()){
                        groupName.append(convert(treeStack.peek().children.remove(treeStack.peek().children.size()-1)));
                    }
                    //Stack top is : ..... | PARENTHESIS | ALTERNATIVE so in order to get PARENTHESIS tree we get (n-2)th element
                    treeStack.get(treeStack.size()-2).label.groupName = groupName.toString();
                } else
                    treeStack.peek().add(new Tree<>(new PatternLabel(EnumPatternType.CHAR, s1.charAt(c))));
            }else{
                treeStack.peek().add(new Tree<>(new PatternLabel(EnumPatternType.CHAR, s1.charAt(c))));
            }
            c++;
        }
        if (treeStack.size() == 1)
            return treeStack.pop();
        else throw new Exception("Bracket or parenthesis error in : " + s1);
    }


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

    public enum EnumPatternType {
        BRACKETS,
        PARENTHESIS,
        SPACE,
        CHAR
    }

    public static class PatternLabel {

        private EnumPatternType patternType;
        private char content;
        private String groupName;

        public PatternLabel(EnumPatternType patternType) {
            this.patternType = patternType;
        }

        public PatternLabel(EnumPatternType patternType, char content) {
            this.patternType = patternType;
            this.content = content;
        }

        public EnumPatternType getPatternType() {
            return patternType;
        }

        public void setPatternType(EnumPatternType patternType) {
            this.patternType = patternType;
        }

        public char getContent() {
            return content;
        }

        public void setContent(char content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return patternType +
                    (content == 0 ? "" : " '" + content + "'");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PatternLabel that = (PatternLabel) o;
            return patternType == that.patternType && Objects.equals(content, that.content);
        }

        @Override
        public int hashCode() {
            return Objects.hash(patternType, content);
        }
    }


}
