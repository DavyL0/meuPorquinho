import java.util.*;

public class Teste {

    public int[] twoSum(int[] nums, int target) {

        Map<Integer, Integer> indices = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {

            int complement = target - nums[i];

            if (indices.containsKey(complement)) {
                return new int[]{indices.get(complement), i};
            }

            indices.put(nums[i], i);

        }

        return new int[0];
    }

    public int missingInteger(int[] nums) {
        HashSet<Integer> set = new HashSet<>();

        for (int num : nums) {
            if (num > 0) {
                set.add(num);
            }
        }

        int missing = 1;

        while (set.contains(missing)) {
            missing++;
        }

        return missing;
    }

    public static int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet<>();
        int maxLength = 0;
        int left = 0; // Ponteiro da esquerda

        // Ponteiro da direita expande a janela a cada iteração
        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);

            // Se o caractere já existe, encolhe a janela pela esquerda
            while (set.contains(currentChar)) {
                set.remove(s.charAt(left));
                left++;
            }

            // Adiciona o novo caractere e calcula o tamanho atual da janela
            set.add(currentChar);
            maxLength = Math.max(maxLength, right - left + 1);
        }
  
        return maxLength;
    }

    public boolean containsDuplicate(int[] nums) {

        HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {

            if (map.containsKey(nums[i])) {
                return true;
            }

            map.put(nums[i], i);
        }

        return false;

    }

    public boolean isAnagram(String s, String t) {
        char[] a = s.toCharArray();
        char[] b = t.toCharArray();

        Arrays.sort(a);
        Arrays.sort(b);

        return Arrays.equals(a, b);
    }

    public List<List<String>> groupAnagrams(String[] strs) {
        HashMap<String, List<String>> map = new HashMap<>();

        for (String str : strs) {
            char[] check2 = str.toCharArray();
            Arrays.sort(check2);

            String key = new String(check2);

            if (!map.containsKey(key))
                map.put(key, new ArrayList<>());

            map.get(key).add(str);

        }
        return new ArrayList<>(map.values());
    }

    public int search(int[] nums, int target) {

        for(int i = 0; i < nums.length; i++){
            if(nums[i] == target){
                return i;
            }
        }
        return -1;
    }

    public boolean isValid(String s) {

        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {

            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {

                if (stack.isEmpty()) {return false;}

                char top = stack.pop();

                if (c == ')' && top != '(') {return false;}

                if (c == ']' && top != '[') {return false;}

                if (c == '}' && top != '{') {return false;}
            }
        }

        return stack.isEmpty();
    }

}
