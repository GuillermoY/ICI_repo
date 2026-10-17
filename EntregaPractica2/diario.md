Nombres de los integrantes : Guillermo Isaac Ramos Medina y Liling Chen

# **Diario de la Práctica 2**

Medimos siempre 50 partidas contra:

- R1: rival-basico
- R2: rival-intermedio
- R3: rival-avanzado
  
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

#### **1) MsPacManV0Me v.1.0** – Sábado 17 de octubre del 2026 (línea base)

Codigo : [MsPacManV0Me](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/MsPacManEntrega.java)

# falta poner bien el archivo 

Contenido/Cambios:
Implementamos una maquina de estados base con los siguientes superestados: Con poder y sin poder. Esta máquina de estados incluye modificaciones en "combinarCoste" para que los costes de los nodos tengan prioridad según su estado. 
<br>Dentro de cada superestado se tienen los siguientes estados principales:

* Con poder:
  * <strong>Normal</strong>: Si no hay fantasmas comestibles a 30 nodos o menos de distancia se comerá el mayor número de píldoras posible.
  * <strong>Huir</strong>: Si hay algún fantasma sin vulnerabilidad a 30 nodos o menos de distancia. Se incrementará el coste de los caminos seguros con intersecciones.
  * <strong>Caza</strong>: Se comerán fantasmas a 30 nodos o menos de distancia antes de su tiempo de vulnerabilidad. Se incrementará el coste de los fantasmas comestibles.
* Sin poder:
    * <strong>Normal</strong>: Si no hay fantasmas a 30 nodos o menos de distancia se comerá el mayor número de píldoras posible.
    * <strong>Huir</strong>: Si hay más de 2 fantasmas persiguiendo activamente al PacMan. Se incrementará el coste de los caminos seguros con intersecciones.

Apoyo de IA: No

Partida MsPacManV4:

| Rival | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 12080.0 ± 1260.0 | 6376.8 | 2609.611308925808 |
| R2 | 15680.0 ± 1460.0 |  6186.6  | 3274.732336779759 |
| R3 | 6430.0 ± 1100.0 | 2797.8 | 1055.9632221486752 |

Partida MsPacManV0Me:

| Rival | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 12630.0  ± 1250.0 | 7137.4 | 3160.00375225817 |
| R2 | 18570.0 ± 1400.0 | 7501.0 | 4271.055525993832 |
| R3 |  7230.0 ± 1190.0  | 3150.4 |  1390.6846940868181 |

Tiempo de partida MsPacManV4:

| Rival | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 4001.0 ± 97.0 | 2182.08 | 1303.3143118816565 |
| R2 | 4001.0 ± 80.0  | 2092.88 | 1474.1445619028532 |
| R3 | 4001.0 ± 175.0 | 1300.0 | 744.452243503331 |

Tiempo de partida MsPacManV0Me:

| Rival | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 3447.0 ± 64.0 | 1207.94 | 790.1250588175446 |
| R2 | 4001.0 ± 181.0 | 2037.76 | 1322.622912925645 |
| R3 | 2315.0 ± 189.0 | 1306.38 | 504.3864282270009 |

---

**USANDO DE RIVAL R3**

===== MsPacManV4 =====
|Pacman|Media|Min|Max|Desv|Partidas|
|:---:|:---:|:---:|:---:|:---:|:---:|
MsPacManV4 | Media: 2797.8 | Min: 1100.0 | Max: 6430.0 | Desv.: 1055.9632221486752 | N: 50
MsPacManV4 Ticks | Media: 1300.0 | Min: 175.0 | Max: 4001.0 | Desv.: 744.452243503331 | N: 50

===== MsPacManV0Me =====
|Pacman|Media|Min|Max|Desv|Partidas|
|:---:|:---:|:---:|:---:|:---:|:---:|
MsPacManV0Me | Media: 3150.4 | Min: 1190.0 | Max: 7230.0 | Desv.: 1390.6846940868181 | N: 50
MsPacManV0Me Ticks | Media: 1306.38 | Min: 189.0 | Max: 2315.0 | Desv.: 504.3864282270009 | N: 50

**Comparación con la MsPacManV4 y MsPacManV0Me del pacman**

D      = 3150,4 − 2797,8 = +352,6

ET_D   = √(196,67² + 149,33²) ≈ 246,9

2·ET_D = 493,9

|D| = 352,6 < 493,9 → no se distingue de ruido

## **GHOST**
### Versiones

#### **1) GhostV0ME v.1.0** – Sábado 17 de octubre del 2026 (línea base)

Codigo : [MsPacManV0Me](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/MsPacManEntrega.java)

Contenido/Cambios:

Apoyo de la IA:

Partidas
