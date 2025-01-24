function drawBoard(data) {
    console.log(data)

    const totalWidth = document.documentElement.clientWidth;
    const totalHeight = document.documentElement.clientHeight - 160;

    console.log(`Total Dimensions ${totalWidth}, ${totalHeight}`);

    const canvas = document.getElementById('backBoard');

    const gameBoardCanvas = document.getElementById('gameBoard');


    // TODO: Set the canvas width and height based on the board data
    const minDim = Math.min(totalWidth, totalHeight);

    console.log(minDim);

    gameBoardCanvas.width = minDim ;
    gameBoardCanvas.height = minDim ;
    gameBoardCanvas.style.height = `${minDim}px`;
    gameBoardCanvas.style.width = `${minDim}px`;


    const cellSize = gameBoardCanvas.width / data.width;
    sessionStorage.setItem('cellSize', cellSize);
    console.log(`Set Cellsize to : ${cellSize}`)

    canvas.width = gameBoardCanvas.width;
    canvas.height = gameBoardCanvas.height;

    const meeplesCanvas = document.getElementById('meeples');
    meeplesCanvas.width = gameBoardCanvas.width;
    meeplesCanvas.height = gameBoardCanvas.height;

    const movesCanvas = document.getElementById('moves');
    movesCanvas.width = gameBoardCanvas.width;
    movesCanvas.height = gameBoardCanvas.height;

    // TODO: Center the canvas and update the board element width to leave space for subcomponents
    // const newWidth = canvas.width + 2 * offsetX;
    // boardElement.style.width = `${newWidth}px`;
    // console.log('Updated Width:', newWidth);
    // console.log('Updated Width:', boardElement.offsetWidth);


    const ctx = canvas.getContext('2d');

    // Draw the board
    for (let y = 0; y < data.height; y++) {
        for (let x = 0; x < data.width; x++) {
            if (x % 2 === 0 && y % 2 === 0) {
                ctx.fillStyle = 'rgb(172, 172, 172)';
            } else if (x % 2 === 0 || y % 2 === 0) {
                ctx.fillStyle = 'rgb(140, 140, 140)';
            } else {
                ctx.fillStyle = 'rgb(120, 120, 120)';
            }
            ctx.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
        }
    }

    // Draw the grid
    ctx.strokeStyle = 'rgb(190, 190, 190)';
    ctx.lineWidth = 1;
    for (let y = 0; y < data.height; y++) {
        for (let x = 0; x < data.width; x++) {
            ctx.strokeRect(x * cellSize, y * cellSize, cellSize, cellSize);
        }
    }

    ctx.strokeStyle = 'black';
    ctx.lineWidth = 16;
    ctx.strokeRect(0, 0, canvas.width, canvas.height);

    // Draw extreme corners
    ctx.fillStyle = 'black';
    ctx.fillRect(0, 0, cellSize, cellSize);
    ctx.fillRect(canvas.width - cellSize, canvas.height - cellSize, cellSize, cellSize);

    // Draw walls
    for (let y = 0; y < data.height; y++) {
        for (let x = 0; x < data.width; x++) {
            const cell = data.board[y][x];
            if (cell.wallDown) {
                // Draw a wall at the bottom of the cell
                ctx.fillStyle = 'black';
                ctx.fillRect(x * cellSize - ctx.lineWidth / 4, (y + 1) * cellSize - ctx.lineWidth / 4, cellSize + ctx.lineWidth / 2, ctx.lineWidth / 2);
            }
            if (cell.wallRight) {
                // Draw a wall at the right of the cell
                ctx.fillStyle = 'black';
                ctx.fillRect((x + 1) * cellSize - ctx.lineWidth / 4, y * cellSize - ctx.lineWidth / 4, ctx.lineWidth / 2, cellSize + ctx.lineWidth / 2);
            }
        }
    }

    // Draw the beams as light blue circles at the center of the cells
    ctx.fillStyle = 'lightblue';
    data.beams.forEach(beam => {
        ctx.beginPath();
        ctx.arc((beam.x + 0.5) * cellSize, (beam.y + 0.5) * cellSize, cellSize / 3, 0, 2 * Math.PI);
        ctx.fill();
    });

    // Same for the spawns but width light gray
    ctx.fillStyle = 'lightgray';
    data.spawns.forEach(spawn => {
        ctx.beginPath();
        ctx.arc((spawn.x + 0.5) * cellSize, (spawn.y + 0.5) * cellSize, cellSize / 3, 0, 2 * Math.PI);
        ctx.fill();
    });

    // Draw the target like an archery target (concentric red and white circles) the first circle should be the biggest then the second one should be smaller and the third one should be the smallest

    ctx.fillStyle = 'red';
    ctx.beginPath();
    ctx.arc((data.target.x + 0.5) * cellSize, (data.target.y + 0.5) * cellSize, cellSize / 3, 0, 2 * Math.PI);
    ctx.fill();

    ctx.fillStyle = 'white';
    ctx.beginPath();
    ctx.arc((data.target.x + 0.5) * cellSize, (data.target.y + 0.5) * cellSize, cellSize / 4, 0, 2 * Math.PI);
    ctx.fill();

    ctx.fillStyle = 'red';
    ctx.beginPath();
    ctx.arc((data.target.x + 0.5) * cellSize, (data.target.y + 0.5) * cellSize, cellSize / 7, 0, 2 * Math.PI);
    ctx.fill();
}

// function intToRGBColor(intColor) {
//     const red = (intColor >> 16) & 0xFF;
//     const green = (intColor >> 8) & 0xFF;
//     const blue = intColor & 0xFF;
//     return `rgb(${red}, ${green}, ${blue})`;
// }

function drawMeeples(data) {
    const cellSize = sessionStorage.getItem('cellSize');

    const canvas = document.getElementById('meeples');
    const ctx = canvas.getContext('2d');

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    meeplesPos = []

    // Draw the meeple as colored circles at the center of the cells
    data.forEach(meeple => {
        meeplesPos.push(meeple.pos);
        ctx.fillStyle = getMeepleColor(meeple.name);
        // console.log(intToRGBColor(meeple.color))
        ctx.beginPath();
        ctx.arc((meeple.pos.x + 0.5) * cellSize, (meeple.pos.y + 0.5) * cellSize, cellSize / 3, 0, 2 * Math.PI);
        ctx.fill();
    });

    sessionStorage.setItem('meeplesPos', JSON.stringify(meeplesPos))

}


function drawMoves(moves) {
    console.log(moves)
    const cellSize = sessionStorage.getItem('cellSize');

    const canvas = document.getElementById('moves');
    const ctx = canvas.getContext('2d');

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    const selected = sessionStorage.getItem('selectedMeeple');
    const selectedMeeple = JSON.parse(selected);

    moves.basicMoves.forEach(move => {
        console.log(move)
        drawPolygon(ctx, movePolygon(createCursor(cellSize*3/4), move), 'rgb(255, 200, 3)')
    })

    moves.specialMoves.forEach(move => {
        drawPolygon(ctx, movePolygon(createCursor(cellSize*3/4), move),'rgb(228, 250, 142)');
    })


    drawPolygon(ctx, movePolygon(createCursor(cellSize*3/4), selectedMeeple),'rgb(255, 0, 255)');
}
