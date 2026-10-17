Nombres de los integrantes : Guillermo Isaac Ramos Medina y Liling Chen

# **Diario de la Práctica 2**

Medimos siempre 100 partidas contra:

- R1: GhostConComportamientoInicial
- R2: GhostEntrega
- R3: GhostRandom
  
Ejecutor:

     private static void imprimirResultados( String nombre, Stats[] resultados) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("       " + nombre);
        System.out.println("==============================================");

        for (Stats stats : resultados) {

            System.out.println("----------------------------------------------");

            System.out.println("Métrica: " + stats.getDescription());

            System.out.println("Media:   " + stats.getAverage());

            System.out.println("Mínimo:  " + stats.getMin());

            System.out.println("Máximo:  " + stats.getMax());

            System.out.println("Desv.:   " + stats.getStandardDeviation());

            System.out.println("N:       " + stats.getN());
        }

        System.out.println("==============================================");
    } 
    
## **MS PACMAN**
### Versiones

#### **1) MsPacManEntrega v.1.0** – Sábado 17 de octubre del 2026 (línea base)

Codigo : [MsPacManEntrega](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/MsPacManEntrega.java)


Contenido/ Cambios:



Apoyo de IA: No




Partida:

| Rival | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 6860.0 ± 990.0 | 3135.9 | 1212.7971634184998 |
| R2 | 9340.0 ± 830.0 | 3237.8 | 1609.7158165654614 |
| R3 | 11200.0 ± 600.0 | 5641.3 | 2185.1965933332294 |

Tiempo de partida:

| Rival | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 4001.0 ± 97.0 | 2182.08 | 1303.3143118816565 |
| R2 | 4001.0 ± 272.0 | 1984.02 | 1199.306171302913 |
| R3 | 3326.0 ± 106.0 | 1080.05 | 738.0885136895195 |
