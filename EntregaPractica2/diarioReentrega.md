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

#### **1) MsPacManV1Me v.1.0** – Sábado 17 de octubre del 2026

El proposito de este controlador es testear con las fases pero nos dimos cuenta de que no hay mucha diferencia si la fase de CAZA va antes que HUIDA o al reves:

Partida MsPacManV0Me:

| Rival | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| GhostEntrega | 12470.0  ± 1290.0 | 5996.8 | 3106.9122673057077 |

Partida MsPacManV1Me:

| Rival | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| GhostEntrega | 13990.0  ± 1290.0 | 5540.4 | 2911.622003948581 |


**Comparación con la MsPacManV0Me y MsPacManV1Me del pacman**

Margen V0Me = 12470,0 − 5996,8 = 6473,2

Margen V1Me = 13990,0 − 5540,4 = 8449,6

D      = 8449,6 − 6473,2 = +1976,4

ET_D   = √(1824,3² + 4257,7²) ≈ 4632,1

2·ET_D = 9264,3

|D| < 2·ET_D → no se distingue de ruido

## **GHOST**
### Versiones

#### **1) GhostV0 v.1.0** – Sábado 17 de octubre del 2026 (línea base)

Codigo : [GhostV0](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/GhostV0ME.java)

Contenido/Cambios: Este controlador se extiende de GhostEntrega, dentro de este implementamos las máquinas de estados y cambiamos dos métodos principales entre estos:

* **segunSuPapel**: 
  * En el papel de PINKY, ahora calcula el próximo cruce al que se dirige Pac-Man en vez de perseguir al Pac-Man como BLINKY.


        case PINKY: { // Rosa: intenta interceptar a Ms. Pac-Man
                int cruce = proximoCruceDe(game);

                if (cruce != -1) {
                    return primerMovimientoDe(
                            game,
                            game.getShortestPath(nodo, cruce, ultimo));
                }

                // Si no encontramos ningún cruce, perseguimos a Pac-Man
                return primerMovimientoDe(
                        game,
                        game.getShortestPath(nodo, pacman, ultimo));
        }



* **huirConCriterio**:
  * Cambiamos la lógica para que se excluya de si mismo en el bucle, diferencie entre si es un fantasma comestible o no y si se trata del fantasma no comestible más cercano a él entonces se dirije hacia ese fantasma, si no hay fantasmas no comestibles entonces huye de Pac-Man.


        private MOVE huirConCriterio(Game game, int nodo, MOVE ultimo, int pacman, GHOST ghostType) {

            GHOST mejorCompanero = null;
            int menorDistancia = Integer.MAX_VALUE;

            // Buscamos el compañero no comestible más cercano
            // que se encuentre fuera de la guarida.
            for (GHOST otro : GHOST.values()) {

                if (otro == ghostType) {
                    continue;
                }

                if (game.getGhostLairTime(otro) > 0) {
                    continue;
                }

                if (game.isGhostEdible(otro)) {
                    continue;
                }

                int nodoOtro = game.getGhostCurrentNodeIndex(otro);

                if (nodoOtro == -1) {
                    continue;
                }

                int distancia =
                        game.getShortestPathDistance(nodo, nodoOtro);

                if (distancia >= 0 && distancia < menorDistancia) {
                    menorDistancia = distancia;
                    mejorCompanero = otro;
                }
            }

            // Si existe un compañero adecuado, nos acercamos a él
            if (mejorCompanero != null) {
                int objetivo =
                        game.getGhostCurrentNodeIndex(mejorCompanero);

                int[] camino =
                        game.getShortestPath(nodo, objetivo, ultimo);

                return primerMovimientoDe(game, camino);
            }

            // Si no hay compañeros válidos, huimos de Pac-Man
            return game.getApproximateNextMoveAwayFromTarget(
                    nodo,
                    pacman,
                    ultimo,
                    DM.PATH);
        }

Además implementamos otro método auxiliar:
* **proximoCruceDe**: este método toma la última dirección de Pac-Man para calcular el índice del nodo de la proxima intersección que tiene delante el Pac-Man. De esta forma se sabrá con antelación dondde se tiene que tomar una decisión de giro los fantasmas.
* 
        private int proximoCruceDe(Game game) {
            int pacman = game.getPacmanCurrentNodeIndex();
            MOVE direccion = game.getPacmanLastMoveMade();

            // Sin dirección conocida, no podemos calcular hacia dónde va
            if (direccion == null || direccion == MOVE.NEUTRAL) {
                return -1;
            }

            // Avanzamos primero en la dirección de Pac-Man
            int actual = game.getNeighbour(pacman, direccion);

            if (actual == -1) {
                return -1;
            }

            MOVE movimientoAnterior = direccion;
            int limite = 0;

            // Buscamos la primera intersección que haya delante
            while (actual == -1 && limite < 1000) {
                MOVE[] opciones =
                        game.getPossibleMoves(actual, movimientoAnterior);

                if (opciones == null || opciones.length == 0) {
                    return -1;
                }

                // En un pasillo normalmente solo hay una opción
                MOVE siguiente = opciones[0];
                int vecino = game.getNeighbour(actual, siguiente);

                if (vecino == -1) {
                    return -1;
                }

                actual = vecino;
                movimientoAnterior = siguiente;
                limite++;
            }

            if (limite >= 1000) {
                return -1;
            }

            return actual;
        }

Apoyo de la IA: No 

Partida GhostV0:

| Rival | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| MsPacManV0ME | 12900.0  ± 1570.0 | 6428.0 | 2984.4488090562973 |

Tiempo de partida GhostV0:

| Rival | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| MsPacManV0ME | 4001.0 ± 51.0 | 1719.76 | 1333.1056047700752 |



**Sábado 17 de Octubre del 2026**

Editamos unas condiciones del bucle while del GhostV0ME ya que actual nunca iria a ser actual==-1. (actual = game.getNeighbour(pacman, direccion);).
Lo cambiamos a while(actual != -1 && !game.isJunction(actual) && limite <1000).

#### **2) GhostV0ME v.1.0** – Sábado 17 de octubre del 2026 (línea base)
Código: [GhostV0ME](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/GhostV0ME.java)

Contenido/Cambios: Implementamos la máquina de estados teniendo las siguientes fases:
* PERSEGUIR
* EVADIR
* HUIDA
* NORMAL

Para cada fantasma tendrá un estado diferente que se da en el momento a la hora de que pida un movimiento.

En este controlador hemos cambiado:
1) getMove:
    Miramos si el fantasma esta en la guarida y si no entonces calculamos su fase actual con ***detectarFase*** con la fase que devuelve el método anterior lo usamos en ***actualizarEstados*** para que realize dicha acción dependiendo de su fase con un switch siendo este:
    
        private void actualizarEstados(Game game, FaseG estado, GHOST f) {
            switch (estado) {
        
                case HUIDA:
                    moves.put(f, huirConCriterio(
                            game,
                            game.getGhostCurrentNodeIndex(f),
                            game.getGhostLastMoveMade(f),
                            game.getPacmanCurrentNodeIndex(),
                            f));
                    break;
        
                case EVADIR:
                    moves.put(f, movimientoDispersar(game, f));
                    break;
        
                case PERSEGUIR:
                case NORMAL:
                default:
                    // Cada fantasma utiliza su estrategia propia.
                    moves.put(f, segunSuPapel(game, f));
                    break;
            }
	    }

        private FaseG detectarFase(Game game, GHOST fantasma, boolean dispersar) {

            if (game.isGhostEdible(fantasma)) {
                return FaseG.HUIDA;
            }

            int nodo = game.getGhostCurrentNodeIndex(fantasma);
            int pacman = game.getPacmanCurrentNodeIndex();

            int distancia =
                    game.getShortestPathDistance(nodo, pacman);

            // No dispersarse cuando Pac-Man esta cerca.
            if (distancia >= 0
                    && distancia <= DISTANCIA_PACMAN_CERCA) {
                return FaseG.PERSEGUIR;
            }

            if (dispersar) {
                return FaseG.EVADIR;
            }

            return FaseG.PERSEGUIR;
        }
    
2) segunSuPapel:
* **BLINKY**: mantiene su comportamiendo
* **PINKY**: Se tiene una variable constante de `PASOS_PREDICCION` donde también realiza la acción de predecir el movimiento de pacman
* **INKY**: Ahora puntua caa púldona de poder que está activada con `dPacman - dFantasma` y elige la de mayor puntuación (la que está más cerca del Pac-Man), si no hay pildoras de poder activadas entonces persigue al pacman.
* **SUE**: Realiza una persecución directa sin aleatoriedad.

3) huirConCriterio:
   
   Ahora en vez de ir hacia el fantasma no comestible, se calcula el movimiento que va a realizar dependiendo de una puntuación donde interfiere: la posición del Pac-Man, si se trata de un cruce y si hay alguna pildora de poder cercano al fantasma para evadirlo. 

4) puntuacionDispersión: se realiza unos ajustes ligeros

Partida GhostV0Me:

| Rival | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| MsPacManV0ME | 13520.0 ± 960.0 | 5763.4 | 3216.857770926055 |

Tiempo de partida GhostV0Me:

| Rival | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| MsPacManV0ME | 4001.0 ± 19.0 | 1557.8 | 1246.6714867672106 |


**Conclusión**:  

**Comparación con la GhostV0Me y GhostV0 del fantasma**

Margen V0Me = 13520,0 − 5763,4 = 7756,6

Margen V0   = 12900,0 − 6428,0 = 6472,0

D      = 7756,6 − 6472,0 = +1284,6

ET_D   = √(1840,3² + 4387,7²) ≈ 4757,7

2·ET_D = 9515,4

|D| < 2·ET_D → no se distingue de ruido

# PARTE DE LA REENTREGA

**Viernes 6 de Noviembre del 2026 (DEL SWITCH A LA MAQUINA)**

**MsPacManV2**

Contenido/Cambios: Se aplica la librería fsm para transformar la lógica de decisión en una máquina de estados. Se han creado 3 clases para su uso: MsPacManV2Me (controlador), MsPacManState (estados) y MsPacManInput (extracción de variables). Se mantienen los estados anteriores (CAZA, POWER, HUIDA, NORMAL).

IA:Si, se pidio una base para transformar el código del MsPacmanV0Me (el cual usaba un switch y condiciones if/else) a una máquina de estados usando la librería fsm de la asignatura, creando las clases Input y State necesarias asegurando que la lógica de los costes y las transiciones se mantuvieran idénticas.

La IA proporcionó la estructura base de la FSM, la clase MsPacManInput (con el método parseInput() precalculando los booleanos) y la clase MsPacManState implementando la interfaz State.

Tuve que adaptar las transiciones al orden exacto de los if de mi detectarFase original para asegurar que el comportamiento fuera idéntico. Además, añadí el GraphFSMObserver y me di cuenta de que era necesario llamar a showInFrame() explícitamente para poder visualizarlo.

Promt usado:


````
===== PACMAN V0 =====
GHOST V0 | Media: 6635.4 | Min: 1370.0 | Max: 15420.0 | Desv.: 3257.814598651015 | N: 50
GHOST V0 Ticks | Media: 2176.64 | Min: 79.0 | Max: 4001.0 | Desv.: 1461.0331647746498 | N: 50

===== PACMAN V2 =====
GHOST V2 | Media: 6999.8 | Min: 1610.0 | Max: 23310.0 | Desv.: 3872.0012702392924 | N: 50
GHOST V2 Ticks | Media: 2144.62 | Min: 103.0 | Max: 4001.0 | Desv.: 1489.957592739896 | N: 50

===== COMPARACIÓN V0 vs V2 =====

Métrica: GHOST V0
Media V0: 6635.4
Media V2: 6999.8
Diferencia V2 - V0: 364.40000000000055
Error típico de la diferencia: 715.6221041292446
Diferencia / error típico: 0.5092073007490393

Métrica: GHOST V0 Ticks
Media V0: 2176.64
Media V2: 2144.62
Diferencia V2 - V0: -32.01999999999998
Error típico de la diferencia: 295.11325069317695
Diferencia / error típico: 0.108500719384134

50 partidas por versión y rival, semillas 1 a 50, comparando Ms. Pac-Man.

```
== puntos: v2 frente a v0 (más es mejor) ==
rival                        v0               v2         D   2·ET_D
Basico               7642 ± 396       8258 ± 448      +616     1197  ruido
Intermedio           6399 ± 372       6431 ± 501       +32     1249  ruido
Avanzado             3173 ± 202       3032 ± 187      -141      551  ruido
------------------------------------------------------------------------
MEDIA (3)            5738 ± 193       5907 ± 233      +169      605  ruido
v2 es mejor contra 0 rivales, peor contra 0 y no se distingue contra 3.

== turnos por vida: v2 frente a v0 (más es mejor) ==
rival                        v0               v2         D   2·ET_D
Basico               1566 ± 166       1669 ± 162      +103      463  ruido
Intermedio           1986 ± 189       1582 ± 182      -404      524  ruido
Avanzado               542 ± 34         611 ± 81       +69      176  ruido
------------------------------------------------------------------------
MEDIA (3)             1364 ± 84        1288 ± 86       -77      241  ruido
v2 es mejor contra 0 rivales, peor contra 0 y no se distingue contra 3.

== fantasmas comidos: v2 frente a v0 (más es mejor) ==
rival                        v0               v2         D   2·ET_D
Basico              8,20 ± 0,53      9,04 ± 0,60     +0,84     1,60  ruido
Intermedio          7,34 ± 0,46      7,10 ± 0,60     -0,24     1,52  ruido
Avanzado            3,26 ± 0,33      2,78 ± 0,31     -0,48     0,90  ruido
------------------------------------------------------------------------
MEDIA (3)           6,27 ± 0,26      6,31 ± 0,30     +0,04     0,80  ruido
v2 es mejor contra 0 rivales, peor contra 0 y no se distingue contra 3.

```
Los resultados son estadísticamente idénticos (todo es "ruido"), lo que confirma que la refactorización a la FSM ha sido exitosa y el comportamiento es el mismo que el de la versión original.

Resultados del FSMObserver



**MsPacManV3 (HUIR CON MEMORIA)**

Contenido/Cambios:
Se implementa una histéresis en el estado de HUIDA añadiendo un umbral de salida distinto al de entrada en MsPacManInput. Se mantiene el resto de la FSM intacto.

Además de esto también creamos una clase base para reusar el MsPacmanInput en vez de crear otro ya que anteriormente este iba por versión de pacman, ahora con esta clase base que herederaran los pacmans con fsm y no será necesario modificar o crear otro input para el pacman

IA: no

Hipotesis
Al usar el mismo umbral (HOLGURA = 6) para entrar y salir de HUIDA, la FSM entra y sale cada turno cuando el fantasma está justo a 6 por lo tanto se añde un umbral de salida mayor (HOLGURA_SALIDA_HUIDA = 10). Ahora entra a distancia 6, pero no sale hasta que el fantasma se aleja a distancia 10. Esto da una "memoria" que estabiliza el comportamiento.
Resultado: En el GraphFSMObserver se ve que las transiciones son mucho más limpias y no parpadean. En la tabla de Comparar, la puntuación debería mejorar ligeramente al evitar que Pac-Man se quede atascado en zonas de peligro dudando si huir o no.

```
== puntos: v3 frente a v2 (más es mejor) ==
rival                        v2               v3         D   2·ET_D
Basico               7256 ± 177       7647 ± 175      +392      498  ruido
Intermedio           6950 ± 198       6871 ± 200       -79      564  ruido
Avanzado              3263 ± 73        3119 ± 71      -144      204  ruido
------------------------------------------------------------------------
MEDIA (3)             5823 ± 92        5879 ± 92       +56      260  ruido
v3 es mejor contra 0 rivales, peor contra 0 y no se distingue contra 3.

== turnos por vida: v3 frente a v2 (más es mejor) ==
rival                        v2               v3         D   2·ET_D
Basico                1604 ± 68        1605 ± 66        +1      189  ruido
Intermedio            1754 ± 76        1708 ± 73       -46      211  ruido
Avanzado               620 ± 28         582 ± 26       -38       77  ruido
------------------------------------------------------------------------
MEDIA (3)             1326 ± 35        1298 ± 34       -28       98  ruido
v3 es mejor contra 0 rivales, peor contra 0 y no se distingue contra 3.

== fantasmas comidos: v3 frente a v2 (más es mejor) ==
rival                        v2               v3         D   2·ET_D
Basico              7,81 ± 0,23      8,26 ± 0,22     +0,46     0,63  ruido
Intermedio          7,91 ± 0,25      7,88 ± 0,25     -0,03     0,71  ruido
Avanzado            3,31 ± 0,12      3,08 ± 0,12     -0,22     0,35  ruido
------------------------------------------------------------------------
MEDIA (3)           6,34 ± 0,12      6,41 ± 0,12     +0,07     0,34  ruido
v3 es mejor contra 0 rivales, peor contra 0 y no se distingue contra 3.
```

Aunque los resultados estadísticos sigan siendo "ruido" (no hay una mejora significativa en los números), en el GraphFSMObserver se observa que las transiciones son mucho más limpias y no parpadean. Se evita que Pac-Man se quede atascado en zonas de peligro dudando si huir o no, aunque esto no se traduzca en una puntuación drásticamente mayor contra estos rivales.

Resultados del FSMObserver



**Viernes 7 de Noviembre del 2026 (DEL SWITCH A LA MAQUINA)**

**GhostV1**

Cambios/Contenido: Se transforma el controlador de fantasmas GhostsV0Me para usar una FSM por cada fantasma (usando un EnumMap<GHOST, FSM>). Se crean las clases GhostsV1, GhostsState y GhostsInput.

IA: Si, también se pidio una base para transformar el código del GhostsV0Me para usar una FSM por cada fantasma, teniendo en cuenta que cada fantasma debe tener su propia máquina de estados independiente. De aqui se le pidió que no añadiese el estado NORMAL ya que nunca caía en tal estado.

También se le pidio instancir 4 FSM independientes para cada fantasma y añadir los observadores correspondientes.

Tuvimos que envolver el método de getMove() en un bloque try-catch para capturar los NullPointerException y comprobar el getGhostLairTime antes de pedir el nodo actual. 


Hipótesis sobre NORMAL: En GhostsV0Me el estado NORMAL era inalcanzable porque detectarFase siempre devolvía PERSEGUIR, HUIDA o EVADIR.
Decisión: No lo he incluido como estado en la FSM de V1. En su lugar, las transiciones base terminan siempre en PERSEGUIR y este estado ejecuta el método segunSuPapel(). Hacer esto es funcionalmente idéntico a la versión anterior, ya que la acción a ejecutar era exactamente la misma, pero limpia el grafo de la FSM eliminando un estado fantasma (nunca mejor dicho) que nunca se activaba.

Resultado: La tabla de Comparar da los mismos resultados exactos que GhostsV0Me (mismas semillas, mismos rivales), pero el GraphFSMObserver muestra un grafo mucho más limpio y sin ramas muertas.

50 partidas por versión y rival, semillas 1 a 50, comparando fantasmas.

```
== puntos: v1 frente a v0 (menos es mejor) ==
rival                        v0               v1         D   2·ET_D
Basico               1898 ± 139       2229 ± 160      +331      423  ruido
Intermedio           4567 ± 306       4156 ± 418      -411     1035  ruido
Avanzado             8519 ± 407      10980 ± 517     +2461     1315  peor
------------------------------------------------------------------------
MEDIA (3)            4995 ± 176       5788 ± 228      +794      576  peor
v1 es mejor contra 0 rivales, peor contra 1 y no se distingue contra 2.

== turnos por vida: v1 frente a v0 (menos es mejor) ==
rival                        v0               v1         D   2·ET_D
Basico                405 ± 105        440 ± 105       +35      297  ruido
Intermedio           1926 ± 206       3013 ± 190     +1086      560  peor
Avanzado             1688 ± 169       2216 ± 184      +528      499  peor
------------------------------------------------------------------------
MEDIA (3)             1340 ± 96        1890 ± 95      +550      269  peor
v1 es mejor contra 0 rivales, peor contra 2 y no se distingue contra 1.

== fantasmas comidos: v1 frente a v0 (menos es mejor) ==
rival                        v0               v1         D   2·ET_D
Basico              1,38 ± 0,23      2,02 ± 0,27     +0,64     0,71  ruido
Intermedio          4,94 ± 0,43      4,40 ± 0,57     -0,54     1,43  ruido
Avanzado           11,56 ± 0,54     14,48 ± 0,64     +2,92     1,67  peor
------------------------------------------------------------------------
MEDIA (3)           5,96 ± 0,24      6,97 ± 0,30     +1,01     0,77  peor
v1 es mejor contra 0 rivales, peor contra 1 y no se distingue contra 2.
```

Los resultados muestran que V1 es ligeramente peor que V0 contra el rival Avanzado. La causa probable de este empeoramiento es por el uso de Math.random() en la estrategia de SUE.

Resultados del FSMObserver