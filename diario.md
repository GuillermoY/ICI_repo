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

Promt a la IA

Problema: el Pacman no puede distinguir entre "fantasma a 5" de "fantasma a 25" de él.

Por qué: `caminoSeguro` devuelve solamente true o false, por lo tanto es mejor cmbiarlo a una función que devuelva un número para que el Pacman pueda medir cuánto peligro hay y actuar en consecuencia.

| Rival | puntos |fantasmas/píldora | cambios estado|
| :---: | :---: | :---: | :---: |
|R1 |XXXX ± XX |X,X |XX |
|R2 |XXXX ± XX |X,X |XX |
|R3 |XXXX ± XX |X,X |XX |


#### **2) MsPacManFantasmasPeligro v.2.0** – Jueves 8 de octubre del 2026

Codigo : [MsPacManFantasmasPeligro](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/MsPacManFantasmasPeligro.java)

Lo que contiene: Sustituimos la desición secuencial por un mapa de pesos por nodo en el que se tiene por un laddo un peligro gradual y el apetito de los comestibles.

Promt a la IA

Problema: el uso de `Map<Integer,Double>` y N llamadas a `getShortestPathDistance` es lento y sin lookahead, entra en callejones cerrados sin tener en cuenta si hay o no un fantasma en tal intersección.

Por qué: porque cada nodo consulta la distancia a cada píldora y a cada fantasma por separado.

| Rival | puntos |fantasmas/píldora | cambios estado|
| :---: | :---: | :---: | :---: |
|R1 |XXXX ± XX |X,X |XX |
|R2 |XXXX ± XX |X,X |XX |
|R3 |XXXX ± XX |X,X |XX |

Comparación con v1 (R2):

D = XXXX − XXXX = XXX

  ET_D ≈ XX

  2·ET_D = XX

  → [mejora real / no]

#### **3) MsPacManEntrega v.3.0** – Jueves 8 de octubre del 2026 (concurso)

Codigo : [MsPacManEntrega](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/MsPacManEntrega.java)

Lo que contiene: Tiene unos arrays nativos, una busqueda en anchura, los costes de intersecciones, penalizaciones en los callejones y un lookahead de profundidad 3.

Promt a la IA

| Rival | puntos |fantasmas/píldora | cambios estado|
| :---: | :---: | :---: | :---: |
|R1 |XXXX ± XX |X,X |XX |
|R2 |XXXX ± XX |X,X |XX |
|R3 |XXXX ± XX |X,X |XX |

Comparación con v1 (R2):

D = XXXX − XXXX = XXX

  ET_D ≈ XX

  2·ET_D = XX

  → [mejora real / no]


## **GHOST**

### Versiones

#### **1) GhostConComportamientoInicial v.1.0** – Jueves 8 de octubre del 2026 (línea base)

Codigo : [GhostConComportamientoInicial](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/GhostsConComportamientoInicial.java)

Lo que contiene: Cada fantasma persigue a Pacman mientras que INKY campea por la power pill más cercana al Pacman y SUE realiza un movimiento aleatorio cada 20 ticks y en la huida es in movimiento neutral.

Promt a la IA: no se use IA para este controlador

Problema: los fantasmas no se dispersan, por lo tanto son presa fácil para el Pacman cuando estas se vuelven vulnerables y cuanddo son comestibles no huyen.

| Rival (MsPacMan) | puntos que deja hacer |fantasmas/píldora | cambios estado|
| :---: | :---: | :---: | :---: |
|MsPacMan P1 v1 |XXXX ± XX |X,X |XX |
|MsPacMan P1 v2 |XXXX ± XX |X,X |XX |
|MsPacMan P1 v3 |XXXX ± XX |X,X |XX |

#### **2) GhostEntrega v.2.0** – Jueves 8 de octubre del 2026 (concurso)

Codigo : [GhostEntrega](https://github.com/GuillermoY/ICI_repo/blob/8117dca11b6d916295149dd04698fef361a7db90/Li/P1Viejo/GhostsEntrega.java)

Lo que contiene: Tiene una función de dispersión cuanddo están a menos de 5 nodos entre sí con una huida real, no neutral, con `getApproximateNextMoveAwayFromTarget`, además de que tiene una preferencia por intersecciones.

Promt a la IA

Pendiente: bug en `huirConCriterio`

| Rival (MsPacMan) | puntos que deja hacer |fantasmas/píldora | cambios estado|
| :---: | :---: | :---: | :---: |
|MsPacMan P1 v1 |XXXX ± XX |X,X |XX |
|MsPacMan P1 v2 |XXXX ± XX |X,X |XX |
|MsPacMan P1 v3 |XXXX ± XX |X,X |XX |

## **CONCLUSIÓN**


