public class Translated {
  public static void main(String[] args) {
     System.out.println(cond_repeat("yes", name()));
    System.out.println(cond_repeat("no?", "Jane"));

  }

 public static String name() {
  return "John";
 }

 public static String repeat(String x) {
  return x + x;
}

 public static String cond_repeat(String c, String x) {
  return (new StringBuilder("?").reverse().toString().startsWith(new StringBuilder(c).reverse().toString())? repeat(x) : x);
}

}
