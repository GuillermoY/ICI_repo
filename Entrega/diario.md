# Diario Diseño Práctica 2

Para realizar los estados no necesitaremos hacer uso de la IA porque ya tenemos un sistema que es adaptable a nuestro diseño actual. <br> 
Para el PacMan hemos hecho dos superestados, sin poder y con poder.
* El estado sin poder contendrá dos estados:
    * <strong>Normal</strong>: Con la que se comenzará al principio de la partida y que priorizará comer píldoras normales para conseguir puntuación. Si se ve amenazado por algún fantasma pasasrá al estado de Huir.
    * <strong>Huir</strong>: que priorizará las intersecciones y callejones con varias salidas. Además de intentar buscar un PowerPill a 30 nodos de distancia o menos. Si se aleja lo suficiente de los fantasmas pasará al estado normal.
* Cuando se come una PowerPill se pasará al estado con poder:
    * <strong>Caza</strong>: Este estado será con el que se comienza al comer una PowerPill y priorizará comer fantasmas a 30 nodos o menos antes mientras sean vulnerables. Si no hay cercanos o se pasó el tiempo de invulnerabilidad se pasará al estado normal.

Para los fantasmas también hemos planteado dos superestados:
* <strong>No Comestible</strong>: con el que se comenzará al principio y tendrá:
    * Normal: que asignará a cada fantasma un comportamiento, estos pueden variar a: Perseguir al PacMan con desvíos ocasionales, perseguir al PacMan directamente o rodear las PowerPills. Sin embargo, se pasará al estado de persecución si se puede interceptar al PacMan para lograr acabar la partida.
* <strong>Comestible</strong>, si el PacMan se comiese una PowerPill todos los fantasmas pasarán a este estado, hasta que sea comido o se pase su tiempo de vulnerabilidad.
    * <strong>Huida</strong>: Se comenzará por este estado al ser comestible, en él se evitarán caminos con PowerPills, se priorizarán caminos con intersecciones para evitar ser comidos y, se dirigirán a los fantasmas no comestibles, si hay.