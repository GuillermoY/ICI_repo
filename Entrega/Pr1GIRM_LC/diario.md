Nombres de los integrantes : Guillermo Isaac Ramos Medina y Liling Chen

# **DIARIO · Practica 1 (redone) · Grupo GIRM-LC**

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

#### **1) MsPacManNodosInicial v.1.0** – Jueves 8 de octubre del 2026 (línea base)

Codigo : [MsPacManNodosInicial](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/MsPacManNodosInicial.java)

Lo que contiene: Su arquitectura base toma es una decisión secuencial (weakGhost → powerPill → pill segura → huir) con `caminoSeguro` que devuelve true o false.

Apoyo de IA: Si

Prompt : Escribe solo el método private MOVE weakGhost(Game game, int nodo, MOVE ultimo) para un controlador de Pacman. Archivos que usamos: pacman.game.Game, pacman.controllers.PacmanController, pacman.game.Constants.MOVE, pacman.game.Constants.GHOST.

El método busca al fantasma comestible más cercano y devuelve el primer movimiento del camino más corto hacia él. Condiciones que debe cumplir un fantasma para ser objetivo:

1) game.getGhostEdibleTime(f) > 0

2) La distancia (game.getShortestPathDistance(nodo, nf, ultimo)) debe ser >= 0 y <= CERCA (constante CERCA = 30)

3) d + FANTASMAS_PARA_PILDORA < game.getGhostEdibleTime(f) (constante FANTASMAS_PARA_PILDORA = 3)

4) El camino (game.getShortestPath(nodo, nf, ultimo)) debe pasar caminoSeguro(game, camino, f) (método booleano ya existente)

Se devuelve la del menor distancia. El movimiento se obtiene con game.getMoveToMakeToReachDirectNeighbour(camino[0], camino[1]). Si ninguno cumple, devuelve null.

La respuesta:

    private MOVE weakGhost(Game game, int nodo, MOVE ultimo) {
      final int CERCA = 30;
      final int FANTASMAS_PARA_PILDORA = 3;

      GHOST mejorFantasma = null;
      int mejorDistancia = Integer.MAX_VALUE;
      int[] mejorCamino = null;

      for (GHOST f : GHOST.values()) {
          if (game.getGhostEdibleTime(f) > 0) {
              int nf = game.getGhostCurrentNodeIndex(f);
              int d = game.getShortestPathDistance(nodo, nf, ultimo);

              if (d >= 0 && d <= CERCA && d + FANTASMAS_PARA_PILDORA < game.getGhostEdibleTime(f)) {
                  int[] camino = game.getShortestPath(nodo, nf, ultimo);
                  if (camino != null && camino.length >= 2 && caminoSeguro(game, camino, f)) {
                      if (d < mejorDistancia) {
                          mejorDistancia = d;
                          mejorFantasma = f;
                          mejorCamino = camino;
                      }
                  }
              }
          }
      }

      if (mejorFantasma == null || mejorCamino == null) {
          return null;
      }

      return game.getMoveToMakeToReachDirectNeighbour(mejorCamino[0], mejorCamino[1]);
    }

Corregido a mano: le cambiamos para que tenga en vez de un vector de mejorCamino a un solo int de la mejor distancia, también separamos la caden de condiciones de los if's dentro el for y en vez de llamarlo mejor fantasma tenemos objetivo. Además pasamos la condición de `if (camino != null && camino.length >= 2)` fuera del recorrido.

Problema: el Pacman no puede distinguir entre "fantasma a 5" de "fantasma a 25" de él.

Por qué: `caminoSeguro` devuelve solamente true o false, por lo tanto es mejor cmbiarlo a una función que devuelva un número para que el Pacman pueda medir cuánto peligro hay y actuar en consecuencia.

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

#### **2) MsPacManFantasmasPeligro v.2.0** – Jueves 8 de octubre del 2026

Codigo : [MsPacManFantasmasPeligro](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/MsPacManFantasmasPeligro.java)

Lo que contiene: Sustituimos la desición secuencial por un mapa de pesos por nodo en el que se tiene por un lado un peligro gradual y el apetito de los comestibles.

Apoyo de IA: No

Problema: el uso de `Map<Integer,Double>` y N llamadas a `getShortestPathDistance` es lento y sin lookahead, entra en callejones cerrados sin tener en cuenta si hay o no un fantasma en tal intersección.

Por qué: porque cada nodo consulta la distancia a cada píldora y a cada fantasma por separado.

Partida:

| Rival | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 6210.0 ± 640.0 | 2074.9 | 893.3700750020066 |
| R2 | 7670.0 ± 650.0 | 2386.7 | 1204.3431966651103 |
| R3 | 8840.0 ± 880.0 | 4130.9 | 1920.4424442867812 |

Tiempo de partida:

| Rival | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 1531.0 ± 74.0 | 807.09 | 396.2070190180783 |
| R2 | 1736.0 ± 107.0 | 842.11 | 430.8943458351794 |
| R3 | 1855.0 ± 87.0 | 862.14 | 478.23095768560296 |

Comparación con v1 (R2):

D      = 2386,7 − 3237,8 = −851,1 <br>
ET_D   = √(120,43² + 160,97²) = √(14503 + 25911) = √40414 ≈ 201,0
2·ET_D = 402,1

|D| = 851,1 > 402,1 → la diferencia es real (v1 es PEOR que v0)

#### **3) MsPacManEntrega v.3.0** – Jueves 8 de octubre del 2026 (concurso)

Codigo : [MsPacManEntrega](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/MsPacManEntrega.java)

Lo que contiene: Tiene unos arrays nativos, una busqueda en anchura, los costes de intersecciones, penalizaciones en los callejones y un lookahead de profundidad 3.

Apoyo de IA : Si

Prompt: Escribe solo el método private *double costeInterseccion(Game game, int interseccion)* para un controlador de Ms. Pac-Man. No escribas la clase completa ni otros métodos.

El controlador calcula un `array coste[]` por nodo que combina distancia a píldoras, peligro de fantasmas, apetito de comestibles y penalización de callejones. Este método añade un coste extra a los nodos que son intersecciones y que resultan peligrosas porque un fantasma no comestible puede llegar antes o casi a la vez que Ms. Pac-Man.

Se tiene las constantes: `HOLGURA = 6` (margen mínimo de ventaja que pacman tiene sobre el fantasma) y `PELIGRO_BASE = 500.0`.

Se tiene los siguientes métodos ya implementados : esInterseccion(int nodo), game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade(), game.getGhostCurrentNodeIndex(g), game.getGhostLastMoveMade(g), game.getGhostLairTime(g), game.isGhostEdible(g), game.getShortestPathDistance(int origen, int destino, MOVE ultimoMovimiento).

Si no se trata de una intersección devuelve 0.0, para cada fantasma de g en GHOST.values() ignora si esta ya muerto, si es comestible y si su nodo es -1. También calcula el tiempo en el que el pacman llega a la intersección y del fantasma, si alguno de los dos es menor a 0, se ignora al fantasma.
El margen al que se calcula es tiempoFantasma - tiempoPacman y si marguen es menor o igual a la `HOLGURA`, entonces ver si es menor a 0 o mayor a 0. En caso del primero entonces se le suma `PELIGRO_BASE * 5.0` sino entonces `PELIGRO_BASE * (HOLGURA + 1 - margen)`.

La respuesta:

    private double costeInterseccion(Game game, int interseccion) {
      final int HOLGURA = 6;
      final double PELIGRO_BASE = 500.0;

      if (!esInterseccion(interseccion)) {
          return 0.0;
      }

      int nodoPacman = game.getPacmanCurrentNodeIndex();

      double coste = 0.0;

      for (GHOST g : GHOST.values()) {
          if (game.getGhostLairTime(g) > 0) {
              continue;
          }
          if (game.isGhostEdible(g)) {
              continue;
          }
          int nodoFantasma = game.getGhostCurrentNodeIndex(g);
          if (nodoFantasma == -1) {
              continue;
          }

          MOVE ultimoFantasma = game.getGhostLastMoveMade(g);

          int tiempoPacman = game.getShortestPathDistance(nodoPacman, interseccion, game.getPacmanLastMoveMade());
          int tiempoFantasma = game.getShortestPathDistance(nodoFantasma, interseccion, ultimoFantasma);

          if (tiempoPacman < 0 || tiempoFantasma < 0) {
              continue;
          }

          int margen = tiempoFantasma - tiempoPacman;

          if (margen <= HOLGURA) {
              if (margen < 0) {
                  coste += PELIGRO_BASE * 5.0;
              } else {
                  coste += PELIGRO_BASE * (HOLGURA + 1 - margen);
              }
          }
      }

      return coste;
    }

Corregido a mano: sustituimos los contsantes locales a constantes de la clase para un mejor manejo en caso de realizar cambios a estos parámetos: `HOLGURA` y `PELIGRO_BASE`.
Incluimos el 0 dentro del margen porque queremos que el pacman pueda evitar el caso en el que el pacman llegue a su vez con el fantasma en la intersección.
Por último cambiamos el nombre de la variable coste a peligroInterseccion además de poner unos comentarios.

Partida:

| Rival | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 9860.0 ± 880.0 | 3494.0 | 1917.2581432553072 |
| R2 | 13210.0 ± 740.0 | 3013.3 | 1710.5873850874154 |
| R3 | 14390.0 ± 940.0 | 6313.9 | 2846.2816058496355 |

Tiempo de partida:

| Rival | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 1707.0 ± 94.0 | 842.74 | 418.7569054458515 |
| R2 | 1809.0 ± 95.0 | 887.1 | 398.7363499452699 |
| R3 | 2134.0 ± 85.0 | 885.62 | 482.8097640715526 |

Comparación con v2 (R2):

D      = 3013,3 − 2386,7 = 626,6<br>
ET_D   = √(171,06² + 120,43²) = √(29261 + 14503) = √43764 ≈ 209,2
2·ET_D = 418,4

|D| = 626,6 > 418,4 → la diferencia es real (v2 es MEJOR que v1)

## **GHOST**

### Versiones

#### **1) GhostConComportamientoInicial v.1.0** – Jueves 8 de octubre del 2026 (línea base)

Codigo : [GhostConComportamientoInicial](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/GhostsConComportamientoInicial.java)

Lo que contiene: Cada fantasma persigue a Pacman mientras que INKY campea por la power pill más cercana al Pacman y SUE realiza un movimiento aleatorio cada 20 ticks y en la huida es in movimiento neutral.

Apoyo de IA: no se usó IA para este controlador

Problema: los fantasmas no se dispersan, por lo tanto son presa fácil para el Pacman cuando estas se vuelven vulnerables y cuanddo son comestibles no huyen.

Partida:

| Rival (MsPacMan) | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| MsPacMan P1 v1 | 6860.0 ± 990.0 | 3135.9 | 1212.7971634184998 |
| MsPacMan P1 v2 | 6210.0 ± 640.0 | 2074.9 | 893.3700750020066 |
| MsPacMan P1 v3 | 9860.0 ± 880.0 | 3494.0 | 1917.2581432553072 |

Tiempo de partida:

| Rival (MsPacMan) | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| MsPacMan P1 v1 | 4001.0 ± 97.0 | 2182.08 | 1303.3143118816565 |
| MsPacMan P1 v2 | 1531.0 ± 74.0 | 807.09 | 396.2070190180783 |
| MsPacMan P1 v3 | 1707.0 ± 94.0 | 842.74 | 418.7569054458515 |

#### **2) GhostEntrega v.2.0** – Jueves 8 de octubre del 2026 (concurso)

Codigo : [GhostEntrega](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/GhostsEntrega.java)

Lo que contiene: Tiene una función de dispersión cuanddo están a menos de 5 nodos entre sí con una huida real, no neutral, con `getApproximateNextMoveAwayFromTarget`, además de que tiene una preferencia por intersecciones.

Apoyo de IA: No

Partida:

| Rival (MsPacMan) | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| MsPacMan P1 v1 | 9340.0 ± 830.0 | 3237.8 | 1609.7158165654614 |
| MsPacMan P1 v2 | 7670.0 ± 650.0 | 2386.7 | 1204.3431966651103 |
| MsPacMan P1 v3 | 13210.0 ± 740.0 | 3013.3 | 1710.5873850874154 |

Tiempo de partida:

| Rival (MsPacMan) | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| MsPacMan P1 v1 | 4001.0 ± 272.0 | 1984.02 | 1199.306171302913 |
| MsPacMan P1 v2 | 1736.0 ± 107.0 | 842.11 | 430.8943458351794 |
| MsPacMan P1 v3 | 1809.0 ± 95.0 | 887.1 | 398.7363499452699 |

## **CONCLUSIÓN**

Para el concurso elegimos MsPacManEntrega (v3) y GhostEntrega (v2). Aunque contra GhostEntrega la media de MsPacManNodosInicial es ligeramente superior (3237,8 vs 3013,3), la diferencia no es estadísticamente significativa (D = 224,5 < 2·ET_D = 469,7). Contra R1 y R3, Entrega sí supera a MsPacManNodosInicial (3494 vs 3136 y 6314 vs 5641). Además, Entrega es mucho más rápida: 842–887 ticks de media frente a 1080–2182 de MsPacManNodosInicial, lo que la hace más viable dentro del presupuesto de 40 ms por turno.

Además de esto, también nos dimos cuenta que si se aumenta el parámetro de la `PROFUNDIDAD_SEGURA` de 3 a 150, nos sales estos resultados:

Partida:

| Rival | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 16480.0 ± 1590.0 | 7430.8 | 3865.755294893871 |
| R2 | 17840.0 ± 1260.0 | 6082.8 | 3472.073894852057 |
| R3 | 13820.0 ± 2000.0 | 8946.6 | 2690.9703931046242 |

Tiempo de partida:

| Rival | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 1831.0 ± 76.0 | 917.41 | 417.5163722671377 |
| R2 | 1805.0 ± 89.0 | 982.06 | 390.38250654216233 |
| R3 | 1764.0 ± 246.0 | 1107.33 | 281.80730055565414 |

Comparación con v3 (PROFUNDIDAD_SEGURA=3):

  R1: D = +3936,8; ET_D ≈ 431,6; 2·ET_D = 863,2 → mejora real

  R2: D = +3069,5; ET_D ≈ 387,1; 2·ET_D = 774,2 → mejora real

  R3: D = +2632,7; ET_D ≈ 391,7; 2·ET_D = 783,4 → mejora real

Problema: el tiempo por turno puede acercarse al límite de 40 ms.
Por qué: 150 niveles de lookahead es casi búsqueda exhaustiva del mapa.

---
## **REENTREGA**


#### **1) Pinky corta el paso** – Miércoles 14 de octubre del 2026 (línea base)

Codigo : [GhostEntrega](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/GhostsEntrega.java)

Lo que contiene: El fantasma PINKY obtiene el cruce más cercano al que se encuentra PacMan. Además, en huir con criterio se buscará al fantasma más cercano para dirigirse hacia él, reduciendo las posibilidades de ser comido.

Apoyo de IA: se usó IA para este avance

Concretamente en la parte de proximoCruceDe para encontrar cuando sería la próxima intersección que se dirige el pacMan para interceptar con él, que realiza algo parecido al pacman viendo cuantos nodos vecinos tiene el nodo intersección.

Partida:

| Rival (MsPacMan) | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| MsPacMan P1 v1 | 7420.0 ± 830.0 | 3151.5 | 1272.6329781086522 |
| MsPacMan P1 v2 | 6000.0 ± 740.0 | 2229.2 | 1018.4318307768051 |
| MsPacMan P1 v3 | 10810.0 ± 1040.0 | 4918.0 | 2061.311242972308 |

Tiempo de partida:

| Rival (MsPacMan) | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| MsPacMan P1 v1 | 4001.0 ± 90.0 | 1637.0 | 935.108809095757 |
| MsPacMan P1 v2 | 1576.0 ± 67.0 | 895.52 | 406.46230533293704 |
| MsPacMan P1 v3 | 4001.0 ± 66.0 | 1644.74 | 1263.133906223131 |

#### **2) Lo bueno de la primera** – Miércoles 14 de octubre del 2026 (línea base)
Codigo : [MsPacManV4](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/MsPacManV4.java)
Codigo : [MsPacManV5](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/MsPacManV5.java)

Lo que contiene: El fantasma PINKY obtiene el cruce más cercano al que se encuentra PacMan. Además, en huir con criterio se buscará al fantasma más cercano para dirigirse hacia él, reduciendo las posibilidades de ser comido.

Apoyo de IA: no

Concretamente en la parte de proximoCruceDe para encontrar cuando sería la próxima intersección que se dirige el pacMan para interceptar con él, que realiza algo parecido al pacman viendo cuantos nodos vecinos tiene el nodo intersección.

Partida:

| Rival (MsPacManV4) | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 14080.0 ± 970.0 | 6703.3 | 3415.9456903756477 |
| R2 | 11710.0 ± 1120.0 | 5028.7 | 2062.1767723461635 |
| R3 | 11280.0 ± 2630.0 | 6687.9 | 1882.8552866067616 |


| Rival (MsPacManV5) | Puntos | Media de puntos | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 13110.0 ± 950.0 | 6028.2 | 3061.391503776337 |
| R2 | 11880.0 ± 1030.0 | 4959.6 | 2332.6215018101557 |
| R3 | 12530.0 ± 2460.0 | 6779.5 | 2176.464733776713 |

Tiempo de partida:

| Rival (MsPacManV4) | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 4001.0 ± 45.0 | 2051.72 | 1389.879386381016 |
| R2 | 4001.0 ± 70.0 | 1802.31 | 1159.944257586144 |
| R3 | 4001.0 ± 37.0 | 1289.91 | 849.0214461723601 |

| Rival (MsPacManV5) | Tiempo | Media de tiempo | Desviación |
| :---: | :---: | :---: | :---: |
| R1 | 4001.0 ± 22.0 | 2121.45 | 1476.819448796445 |
| R2 | 4001.0 ± 14.0 | 1796.49 | 1261.3402643716931 |
| R3 | 4001.0 ± 4.0 | 1099.92 | 837.1648604882814 |


Comparación de V4 y V5:

| Rival  | Media V4 | Media V5 | Diferencia V5 − V4 |
| :---: | :---: | :---: | :---: |
| R1 | 6703,3 | 6028,2 | −675,1 |
| R2 | 5028,7 | 4959,6 | −69,1 |
| R3 | 6687,9 | 6779,5 | +91,6 |

Decisión Final: Con los resultados obtenidos nos quedaremos con la versión V4. La V5 empeora con las versiones con fantasmas más inteligentes pero sorprendentemente mejora con fantasmas impredecibles.