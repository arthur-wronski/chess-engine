// TODO: create an instance of the singleton board class that initiates bitboards array with all the starting positions,

// Index 0: White Pawns
// Index 1: White Knights
// Index 2: White Bishops
// Index 3: White Rooks
// Index 4: White Queen
// Index 5: White King
// Index 6: Black Pawns
// Index 7: Black Knights
// Index 8: Black Bishops
// Index 9: Black Rooks
// Index 10: Black Queen
// Index 11: Black King
mod board;

use board::Board;
use ggez::{
    event,
    glam::*,
    graphics::{self, Color},
    Context, GameResult,
    conf::WindowMode
};

struct MainState {
}


impl event::EventHandler<ggez::GameError> for MainState {
    fn update(&mut self, _ctx: &mut Context) -> GameResult {
        Ok(())
    }

    fn draw(&mut self, ctx: &mut Context) -> GameResult {
        let mut canvas = graphics::Canvas::from_frame(ctx, graphics::Color::from([0.1, 0.2, 0.3, 1.0]));

        let square_size = 100.0;

        for row in 0..8 {
            for col in 0..8 {
                let color = if (row + col) % 2 == 0 {
                    Color::WHITE
                }else{
                    Color::BLACK
                };
                let rectangle = graphics::Mesh::new_rectangle(
                            ctx,
                            graphics::DrawMode::fill(),
                            graphics::Rect::new(square_size * row as f32, square_size * col as f32, square_size, square_size),
                            color,
                )?;
                canvas.draw(&rectangle, Vec2::new(0.0, 0.0));
            }
        }
        canvas.finish(ctx)?;

        Ok(())
    }
}

fn main() -> GameResult {
    let chess_board = Board {
        bitboards : [71776119061217280, 4755801206503243776, 2594073385365405696, 9295429630892703744, 1152921504606846976, 576460752303423488, 65280, 66, 36, 129, 16, 8]
    };

    println!("{:?}", chess_board.bitboards);

    let cb = ggez::ContextBuilder::new("super_simple", "ggez").window_mode(WindowMode::default().dimensions(800.0, 800.0));
    let (ctx, event_loop) = cb.build()?;
    let state = MainState {};
    event::run(ctx, event_loop, state)
}
