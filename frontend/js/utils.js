
function createCursor(size) {
    return [
        {x: 0, y: -size/2},
        {x: size/7, y: -size/7},
        {x: size/2, y: 0},
        {x: size/7, y: size/7},
        {x: 0, y: size/2},
        {x: -size/7, y: size/7},
        {x: -size/2, y: 0},
        {x: -size/7, y: -size/7},
    ]
}

function createCheckMark(size) {
    return [
        {x: 3/10 * size, y: 6/10 * size},
        {x: 8/10 * size, y: 1/10 * size},
        {x: 9/10 * size, y: 2/10 * size},
        {x: 3/10 * size, y: 8/10 * size},
        {x: 1/10 * size, y: 6/10 * size},
        {x: 2/10 * size, y: 5/10 * size},
    ]
}

function movePolygon(polygon, pos) {
    const cellSize = sessionStorage.getItem('cellSize');
    return polygon.map(p => {
        return {
            x: p.x + (pos.x + 0.5) * cellSize,
            y: p.y + (pos.y + 0.5) * cellSize
        }
    });
}

// Function to draw the polygon
function drawPolygon(ctx, polygon, color) {
    console.log(polygon)
    ctx.beginPath();
    ctx.moveTo(polygon[0].x, polygon[0].y); // Start at the first point

    for (let i = 1; i < polygon.length; i++) {
        ctx.lineTo(polygon[i].x, polygon[i].y); // Draw a line to the next point
    }

    ctx.closePath(); // Close the path (back to the start)
    ctx.fillStyle = color;
    ctx.fill(); // Fill the shape

}


function getMeepleColor(meeple) {
    switch (meeple) {
        case "FORREST JUMP" :
            return "rgb(0, 255, 0)";
        case "OZZY MOSIS":
            return "rgb(100, 100, 100)";
        case "BLUE BEAMER":
            return "rgb(0, 0, 255)";
        case "SHORTSTOP" :
            return "rgb(77, 58, 4)";
        case "SIDESTEP":
            return "rgb(255, 0, 0)";
        case "SKEWT":
            return "rgb(255, 255, 255)";
        case "MC EDGE":
            return "rgb(255, 255, 0)";
        case "CARBON":
            return "rgb(0, 0, 0)";
    }
}

function convertTimeToTimer(time) {
    let minutes = Math.floor(time / 60);
    let seconds = time % 60;
    return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
}
