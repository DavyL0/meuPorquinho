//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {

    Teste teste = new Teste();

    //Integer result = teste.missingInteger(new int[]{2,11,15,7});
    //System.out.println(result);

    boolean result = teste.containsDuplicate(new int[]{0});
    System.out.println(result);

    List<List<String>> anagramGroup = teste.groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"});
    System.out.println(anagramGroup);

    Boolean tetas = teste.isValid("({})");

    System.out.println(tetas);

}
