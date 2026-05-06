# Entry Point schützen
-keep public class studyplanner.Main {
    public static void main(java.lang.String[]);
}

# Optimierung und Preverify deaktivieren
-dontoptimize
-dontpreverify

# Obfuskierung
-repackageclasses ''
-allowaccessmodification
-dontusemixedcaseclassnames
-dontwarn
-ignorewarnings

# Mapping aufheben
-printmapping target/mapping.txt
-verbose